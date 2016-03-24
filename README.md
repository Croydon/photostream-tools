# photostream-tools [![Build Status](https://travis-ci.org/aschattney/photostream-tools.svg?branch=master)](https://travis-ci.org/aschattney/photostream-tools)

Diese Bibliothek ist Teil des Praktikums vom Kurs <i>Nutzerzentrierte Softwareentwicklung</i> der Hochschule Darmstadt für das Wintersemester 16/17

## Gradle

Als Abhängigkeit in der build.gradle deklarieren:

```gradle
dependencies {
  ...
  compile 'hochschuledarmstadt.photostream_tools:photostream-tools:0.0.24'
}
```

## Server Adresse

Im Android Manifest:

```xml
...
<application>
  ...
    <meta-data
        android:name="PHOTOSTREAM_URL"
        android:value="http://ip:port" />
  ...
</application>
```

## Verwendung

<a href="https://github.com/aschattney/photostream-tools/wiki/">Wiki</a> lesen und <a href="https://github.com/aschattney/photostream-tools/tree/master/examples">Beispielprojekt</a> ansehen

## License

The MIT License (MIT)
Copyright (c) 2016 Andreas Schattney

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
