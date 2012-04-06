#!/bin/bash

GOOS=linux GOARCH=386 go build -o ../bin/linux_386/spre-qp qp.go
GOOS=linux GOARCH=amd64 go build -o ../bin/linux_amd64/spre-qp qp.go
GOOS=windows GOARCH=386 go build -o ../bin/windows_386/spre-qp.exe qp.go
GOOS=windows GOARCH=amd64 go build -o ../bin/windows_amd64/spre-qp.exe qp.go
GOOS=darwin GOARCH=386 go build -o ../bin/darwin_386/spre-qp qp.go
GOOS=darwin GOARCH=amd64 go build -o ../bin/darwin_amd64/spre-qp qp.go

