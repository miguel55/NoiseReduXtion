#!/bin/bash

#Generación de PDF compilando el archivo LaTeX con pdflatex
pdflatex -synctex=1 -interaction=nonstopmode API_esp.tex
pdflatex -synctex=1 -interaction=nonstopmode API_esp.tex

pdflatex -synctex=1 -interaction=nonstopmode API_spanish.tex
pdflatex -synctex=1 -interaction=nonstopmode API_spanish.tex

rm {*.aux,,*.log,*.out,*.synctex.gz,*toc}
