# NoiseReduXtion

## Descripción
Este repositorio contendrá la librería generada para el Trabajo de Fin de Grado de Realce de la Voz en Dispositivos Móviles con Dos Micrófonos, así como la documentación de la misma.

La librería NoiseReduXtion se usa para el tratamiento de las señales procedentes de un teléfono móvil con un micrófono dual con el objetivo del realce de la voz y la reducción de ruido. La librería puede tratar señales completas de varios segundos o señales por tramas.

El código fuente Java se distribuye en 3 paquetes: common, frame y note. El primero de ellos incluye las clases Complex (operaciones con números complejos), FFT (transformadas directas e inversa y convolución) y MusFilter (filtro para la reducción del ruido musical). 

Los paquetes frame y note tienen la misma estructura. Se componen de: la clase NoiseReduction, de la que heredan Filtering (para algoritmos basados en filtrado de las señales) y PowerLevel (para algoritmos basados en diferencia de potencia de las señales). MVDR y MVDRd extienden de Filtering (implementan el algoritmo MVDR sin y con retardo) y PLD y Sigmoid de PowerLevel. La clase Enhpro implementa el método Overlap-Add.

## Uso

El código se puede incluir en cualquier aplicación de manera separada: o bien los paquetes common y frame si se quieren tratar las señales trama a trama; o bien los paquetes common y note si se quieren tratar señales completas. 

Se incluyen dos aplicaciones de ejemplo, en Java y código nativo (con las librerías correspondientes) para el testeo. Las librerías se pueden incluir en cualquier aplicación.



## Description
This repository will contain the API generated for EOG work called Speech Enhancement for Dual-Microphone Smartphones, and documentation for this one.

NoiseReduXtion API is used for processing the signals captured by a dual-microphone smartphone with the aim of speech enhancement and noise reduction. API can process complete signals (with a few seconds duration) or signals frame to frame.

Java font code is distributed in 3 packages: common, frame and note. The first one includes classes Complex (for operations with Complex numbers), FFT (which implements direct and inverse Fourier transform and convolution) and MusFilter (a filter for musical noise reduction).

Packages frame and note have the same structure. They have: a NoiseReduction class, from which Filtering (for algorithms based on filtering the signals) and PowerLevel (for algorithms based on power difference of the signals) inherit. MVDR and MVDRd extend Filtering (implement MVDR algorithm with and without delay) and PLD and Sigmoid extend PowerLevel. Enhpro class implements Overlap-Add method.

## Use

Given code can be included in any application separately: either packages common and frame if you want to process signals frame to frame or packages common and note if you want to process complete signals.

Two example apps are included, one in Java and other in native code (with corresponding libraries) for test. Libraries can be used in any other application.
