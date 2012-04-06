/*
A tool to take an xml description of SPRE data and output it in the format required by the software.

<item>
	<sentence>
		The visitors at the zoo watched the zookeeper who the monkeys and apes
		teased.
	</sentence>
	<question>

	</question>
	<answer>

	</answer>
	<condition1>
		Relative Clause
	</condition1>
	<condition2>
		High
	</condition2>
</item>

The tool handles items only the sentence component or any combination of the others. The output is a series of files grouping each of the components together this can then be added to the appropriate value files in the android SPRE project.
*/

package main

import (
	"encoding/xml"
	"flag"
	"fmt"
	"os"
	"sort"
	"strings"
)

// Flags
var in *string = flag.String("in", "", "File to parse.")
var out *string = flag.String("out", "new.xml", "Name of file to write to.")
var prefix *string = flag.String("prefix", "", "Prefix to use for xml name attributes e.g. practice for result practice_sentences.")

func init() {
	flag.Usage = func() {
		fmt.Fprintf(os.Stderr, "Usage of %s:\n", os.Args[0])
		fmt.Fprintf(os.Stderr, "  spre-qp [flags] [filename]\n")
		flag.PrintDefaults()
	}
}

// Structure of xml to read in.
type Data struct {
	Items Items `xml:"Item"`
}

type Items []*Item

func (a Items) Len() int { return len(a) }

func (a Items) Swap(i, j int) { a[i], a[j] = a[j], a[i] }

// Implement sort.Interface such that items without questions are last
type QFirst struct {
	Items
}

func (a QFirst) Less(i, j int) bool {
	return a.Items[i].Question > a.Items[j].Question
}

type Item struct {
	Sentence   string
	Question   string
	Answer     string
	Condition1 string
	Condition2 string
	Index      int
}

// Structure of the different xml components to output
type SentenceData struct {
	XMLName   xml.Name `xml:"string-array"`
	Name      string   `xml:"name,attr"`
	Sentences []string `xml:"item"`
}

type QuestionData struct {
	XMLName   xml.Name `xml:"string-array"`
	Name      string   `xml:"name,attr"`
	Questions []string `xml:"item"`
}

type AnswerData struct {
	XMLName xml.Name `xml:"integer-array"`
	Name    string   `xml:"name,attr"`
	Answers []int    `xml:"item"`
}

type Condition1Data struct {
	XMLName     xml.Name `xml:"integer-array"`
	Name        string   `xml:"name,attr"`
	Condition1s []int    `xml:"item"`
}

type Condition2Data struct {
	XMLName     xml.Name `xml:"integer-array"`
	Name        string   `xml:"name,attr"`
	Condition2s []int    `xml:"item"`
}

type IndexData struct {
	XMLName xml.Name `xml:"integer-array"`
	Name    string   `xml:"name,attr"`
	Indices []int    `xml:"item"`
}

// Conditions
var as map[string]int = map[string]int{
	"NO":  0,
	"YES": 1,
}
var c1s map[string]int = map[string]int{
	"Filler":          0,
	"Relative Clause": 1,
	"Adverb":          2,
	"Coordination":    3,
}
var c2s map[string]int = map[string]int{
	"Filler": 0,
	"High":   1,
	"Low":    2,
}

func main() {
	flag.Parse()
	if flag.NArg() == 0 && *in == "" {
		fmt.Fprintf(os.Stderr, "Error need filename\n")
		flag.Usage()
		return
	}
	var filename string
	if *in == "" {
		filename = flag.Args()[0]
	} else {
		filename = *in
	}

	var spreData Data
	readXmlFromFile(filename, &spreData)

	// Strip excess white space caused by xml formatting.
	// and save in indices to preserve across sorting.
	indices := []int{}
	for i := range spreData.Items {
		spreData.Items[i].Index = i
		spreData.Items[i].Sentence = strings.Join(strings.Fields(spreData.Items[i].Sentence), " ")
		spreData.Items[i].Question = strings.Join(strings.Fields(spreData.Items[i].Question), " ")
		spreData.Items[i].Answer = strings.Join(strings.Fields(spreData.Items[i].Answer), " ")
		spreData.Items[i].Condition1 = strings.Join(strings.Fields(spreData.Items[i].Condition1), " ")
		spreData.Items[i].Condition2 = strings.Join(strings.Fields(spreData.Items[i].Condition2), " ")
	}

	// Sort sentences to have those with questions first.
	sort.Sort(QFirst{spreData.Items})

	// Split data
	sentences := []string{}
	questions := []string{}
	answers := []int{}
	condition1s := []int{}
	condition2s := []int{}
	for i := range spreData.Items {
		indices = append(indices, spreData.Items[i].Index)
		sentences = append(sentences, spreData.Items[i].Sentence)
		if spreData.Items[i].Question != "" {
			questions = append(questions, spreData.Items[i].Question)
			answers = append(answers, as[spreData.Items[i].Answer])
		}
		if spreData.Items[i].Condition1 != "" {
			condition1s = append(condition1s, c1s[spreData.Items[i].Condition1])
		} else {
			condition1s = append(condition1s, c1s["Filler"])
		}
		if spreData.Items[i].Condition2 != "" {
			condition2s = append(condition2s, c2s[spreData.Items[i].Condition2])
		} else {
			condition2s = append(condition2s, c2s["Filler"])
		}
	}

	file, err := os.Create(*out)
	if err != nil {
		fmt.Fprintln(os.Stderr, "Error", err)
	}
	encoder := xml.NewEncoder(file)
	if err := encoder.Encode(SentenceData{Sentences: sentences, Name: addPrefix(*prefix, "sentences")}); err != nil {
		fmt.Fprintln(os.Stderr, "Error", err)
	}
	file.Write([]byte("\n"))
	if err := encoder.Encode(Condition1Data{Condition1s: condition1s, Name: addPrefix(*prefix, "conditions1")}); err != nil {
		fmt.Fprintln(os.Stderr, "Error", err)
	}
	file.Write([]byte("\n"))
	if err := encoder.Encode(Condition2Data{Condition2s: condition2s, Name: addPrefix(*prefix, "conditions2")}); err != nil {
		fmt.Fprintln(os.Stderr, "Error", err)
	}
	file.Write([]byte("\n"))
	if err := encoder.Encode(QuestionData{Questions: questions, Name: addPrefix(*prefix, "questions")}); err != nil {
		fmt.Fprintln(os.Stderr, "Error", err)
	}
	file.Write([]byte("\n"))
	if err := encoder.Encode(AnswerData{Answers: answers, Name: addPrefix(*prefix, "answers")}); err != nil {
		fmt.Fprintln(os.Stderr, "Error", err)
	}
	file.Write([]byte("\n"))
	if err := encoder.Encode(IndexData{Indices: indices, Name: addPrefix(*prefix, "indices")}); err != nil {
		fmt.Fprintln(os.Stderr, "Error", err)
	}
	file.Write([]byte("\n"))
	file.Close()

}

func addPrefix(prefix, suffix string) string {
	if prefix == "" {
		return suffix
	}
	return prefix + "_" + suffix
}

func readXmlFromFile(filename string, store interface{}) {
	file, err := os.Open(filename)
	if err != nil {
		fmt.Fprintln(os.Stderr, "Error", err)
	}

	decoder := xml.NewDecoder(file)
	if err := decoder.Decode(store); err != nil {
		fmt.Fprintln(os.Stderr, "Error", err)
	}
	file.Close()
}

