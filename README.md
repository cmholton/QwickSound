# QwickSound

* QwickSound is a Java audio/sound library with an easy to use API for quick 
  playback of WAV, AIFF, AU, MP3, Ogg, and AAC audio files. 
* Functionality includes volume control, pause/resume, looping, 
  position-setting of audio, and querying of playback state.   
* Audio playback can be either preloaded or streamed. In a nutshell, the
  preloaded approach offers the lowest latency and the most functionality,
  while streaming uses the least amount of memory. See the 
  [Javadoc](http://cmholton.github.io/QwickSound/doc) (the PreloadedAudio and 
  StreamingAudio classes) for more detailed information. 
* QwickSound is built on top of Java Sound and utilizes the Tritonus, JLayer,
  JOrbis, and JAAD libraries through Java's SPI.


### How to Setup

* When using QwickSound as a library, the following must be on the classpath: 
    * QwickSound jar (qwicksound-1.x.x.jar found in the dist directory of this 
      repo)
    * The media files that you want to play
    * The third-party audio libraries (those found in the lib-mp3-ogg and 
      lib-aac directories of this repo). 
* NOTE: Not all of the libraries are necessary, depending on the specific
  type(s) of audio formats you plan on playing. See the README-LIBS file in 
  this repo's lib-mp3-ogg directory for specific information.
* NOTE: The JAAD library (located in the lib-aac directory) must be on the end
  of the classpath in order to play AAC files. See the *Known Issues* section
  below for more information.
  
  
### How to Use

```java
// Initialize the QwickSound system.
QwickSound.init();

// Create the audio and begin playback.
StreamingAudio music = QwickSound.createStreamingAudio("theme_song.mp3");
StreamingPlayback musicPlayback = music.play();

// When ready, stop the audio.
musicPlayback.stop();
		
// Shutdown the QwickSound system.
QwickSound.shutdown();
```

### How to Run the Demo

* A demo is included with the source code. To run the demo, execute the 
  "run-demo" default target in the Ant build file. The build file automatically 
  adds the third-party libs and media to the classpath.
* NOTE: Running the demo via Ant causes higher overall CPU usage vs. running
  the demo without Ant.
  

### Repository Organization

* src directory - qwicksound package - The QwickSound API that client code uses
* src directory - qwicksound.util.logging package - Logging configuration code
* src directroy - demo package - Contains a runnable demo
* dist directory - Contains the QwickSound jar
* lib-mp3-ogg directory - Contains third-party libs needed for playback of MP3
  and Ogg audio
* lib-aac directory - Contains the third-party lib needed for playback of AAC
  audio. In its own directory, the lib can be easily added to the end of the
  classpath.
* media directory - Contains some sample audio files used in the demo
* doc directory - Contains the QwickSound Javadoc


### Documentation

* [QwickSound Javadoc](http://cmholton.github.io/QwickSound/doc)


### License

* [LGPL](https://www.gnu.org/licenses/lgpl-3.0.en.html)


### Known Issues

There are no known issues with the QwickSound code. However, there are two
issues that have been encountered when using the JAAD library with QwickSound: 

* JAAD library issue #1 - JAAD does not seem to like all AAC files, at 
  least when it is used as a Java Sound SPI as it is here with QwickSound. 
  Specifically, after testing with a large sample of AAC files (100 files),
  roughly 60% were able to 	be played, and the rest caused an error with 
  JAAD. The exception was m4a files downloaded from iTunes, all of which 
  (25 files tested) played without any problem. 
* JAAD library issue #2 - When used in conjunction with other Java Sound 
  SPI libraries, JAAD will attempt to decode all audio formats, even when the
  input file is an audio format other than AAC, thus preventing the 
  appropriate library from being used. To work around this, a slightly 
  modified version of the JAAD library (located in the lib-aac directory) is 
  used instead. Additionally, the modified JAAD library needs to be on the
  end of the classpath. If it is not on the end of the classpath, AAC files
  will fail to be decoded, however files of other formats should still be 
  properly handled.     


### Contributors

* Christian Holton <cmholton@gmail.com>



