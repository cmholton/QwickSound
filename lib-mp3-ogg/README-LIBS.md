# About the Libraries

SpryAudio will play PCM audio files (WAV, AIFF, AU) without the need for any
extra libraries. To play other audio formats, the following libraries will
need to be on your classpath:

* MP3: tritonus_share-0.3.6.jar, jl1.0.1.jar, mp3spi1.9.5.jar
* Ogg: tritonus_share-0.3.6.jar, jogg-0.0.7.jar, jorbis-0.0.15.jar
* AAC: jaad-0.8.4-modified.jar. Note that the JAAD library must be on the end 
  of the classpath. See the *Known Issues* section in README.md located in 
  this repo's root directory for more info.
        

#### tritonus_share-0.3.6.jar

* *Necessary for both MP3 and Ogg playback*
* Tritonus is an open source implementation of the Java Sound API with plugins
  for MP3 and Ogg Vorbis.
* The Tritonus Share library contains common classes required by all Tritonus
  plugins.
* Available at 
  [http://www.tritonus.org/plugins.html](http://www.tritonus.org/plugins.html)

#### jl1.0.1.jar

* *Necessary for MP3 playback*
* JLayer is a real-time, Java MP3 decoder created by JavaZOOM.
* Available at 
  [http://www.javazoom.net/javalayer/javalayer.html](http://www.javazoom.net/javalayer/javalayer.html)

#### mp3spi1.9.5.jar

* *Necessary for MP3 playback*
* An SPI plugin, based on JLayer, that adds MP3 support to Java Sound.
* Available at [
  http://www.javazoom.net/mp3spi/mp3spi.html](http://www.javazoom.net/mp3spi/mp3spi.html)

#### tritonus_jorbis-0.3.6.jar

* *Necessary for Ogg playback*
* A Tritonus plugin that enables decoding of Ogg Vorbis bitstreams.
* Based on the JOrbis Ogg Vorbis decoder.
* Available at
  [http://www.tritonus.org/plugins.html](http://www.tritonus.org/plugins.html)

#### jogg-0.0.7.jar

* *Necessary for Ogg playback*
* Part of the JOrbis Ogg Vorbis decoder.
* Available at 
  [http://www.jcraft.com/jorbis/](http://www.jcraft.com/jorbis/)

#### jorbis-0.0.15.jar

* *Necessary for Ogg playback*
* Part of the JOrbis Ogg Vorbis decoder.
* Available at 
  [http://www.jcraft.com/jorbis/](http://www.jcraft.com/jorbis/)

#### jaad-0.8.4-modified.jar

* *Necessary for AAC playback*
* An SPI plugin that adds AAC support to Java Sound.
* Original library is available at 
  [http://jaadec.sourceforge.net/index.php](http://jaadec.sourceforge.net/index.php)
* NOTE: Due to a bug in this library (see the *Known Issues* section in
  README.md located in this repo's root directory for more info), SpryAudio 
  uses a slightly modified version. The modified JAAD library is located in 
  the lib-acc directory of this repo.


### License

All the included libraries are licensed under the LGPL. See the LICENSE file
in this (lib) directory for a copy of the LGPL.






