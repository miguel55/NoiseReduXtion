Documentación/Documentation
=

### Instrucciones/Instructions

Para la compilación de los ficheros LaTeX de documentación ejecutar las siguientes líneas de código en Linux, o utilizar un entorno de trabajo (como TeXnicCenter) para ello.

For LaTeX documentation files compilation, execute the following code in Linux or use a work environment (as TeXnicCenter) for this.

```
sudo apt-get install texlive texlive-lang-spanish
wget http://mirrors.ctan.org/macros/latex/contrib/slashbox/slashbox.sty
tlmgr install slashbox.sty
./makePDF.sh
```
