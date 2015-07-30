# SpryAudio

* SpryAudio is a Java audio/sound library with an easy to use API for quick 
  playback of WAV, AIFF, AU, MP3, Ogg, and AAC audio files. 
* Functionality includes volume control, pause/resume, looping, 
  position-setting of audio, and querying of playback state.   
* Audio playback can be either preloaded or streamed. In a nutshell, the
  preloaded approach offers the least latency and the most functionality,
  while streaming uses the least amount of memory. See the Javadoc (the 
  PreloadedAudio and StreamingAudio classes) for more detailed information. 
* SpryAudio is built on top of Java Sound and utilizes the Tritonus, JLayer,
  JOrbis, and JAAD libraries through Java's SPI.


### How to Setup

* When using SpryAudio as a library, the following must be on the classpath: 
    * SpryAudio jar (spryaudio-x.x.x.jar found in the dist directory of this 
      repo)
    * The media files that you want to play
    * The third-party audio libraries (those found in the lib-mp3-ogg and lib-acc 
      directories of this repo). 
* NOTE: The JAAD library (located in the lib-acc directory) must be on the end
  of the classpath in order to play AAC files. See the *Known Issues* section
  below for more information.
* NOTE: Not all of the libraries are necessary, depending on the specific
  type(s) of file formats you plan on playing. See the README-LIBS file in this 
  repo's lib-mp3-ogg directory for specific information.
  
  
### How to Use

```java
// Initialize the SpryAudio system.
SpryAudio.init();

// Create the audio and begin playback.
StreamingSound music = SpryAudio.createStreamingAudio("theme_song.mp3");
StreamingPlayback musicPlayback = music.play();

// When ready, stop the audio and shutdown the SpryAudio system.
musicPlayback.stop();
SpryAudio.shutdown();
```

### How to Run the Demo

* A demo is included with the source code. To run the demo, execute the 
  "run-demo" target in the Ant build file. The build file automatically adds 
  the third-party libs and media to the classpath.
* NOTE: Running the demo via Ant causes higher overall CPU usage vs. running
  the demo without Ant.
  

### Repository Organization

* src directory - spryaudio package - The SpryAudio API that client code uses
* src directory - spryaudio.util.logging package - Logging configuration code
* src directroy - demo package - Contains a runnable demo
* dist directory - Contains the SpryAudio jar
* lib-mp3-ogg directory - Contains third-party libs needed for playback of MP3
  and Ogg audio
* lib-acc directory - Contains the third-party lib needed for playback of ACC
  audio. In its own directory, the lib can be easily added to the end of the
  classpath.
* media directory - Contains some sample audio files used in the demo
* doc directory - Contains the SpryAudio Javadoc


### Documentation

* [SpryAudio Javadoc](http://cmholton.github.io/SpryAudio)


### License

* [LGPL](https://www.gnu.org/licenses/lgpl-3.0.en.html)


### Known Issues

* There are no known issues with the SpryAudio code, however there are a few
  small bugs in the third-party libraries that need to be worked around.
    * JAAD library - When used in conjunction with other Java Sound SPI 
    libraries, JAAD will attempt to decode all audio formats, even when the
    input file is an audio format other than AAC, thus preventing the 
    appropriate library from being used. To work around this, a slightly 
    modified version of the JAAD library (located in the lib-acc directory) 
    is used instead. Additionally, the modified JAAD library needs to be on
    the end of the classpath. If it is not on the end of the classpath, AAC
    files will fail to be decoded, however files of other formats should still
    be properly handled.    
       
* Libraries do not seem to always play all types of files of their type.
* What file types have been tested and work? 


### Contributors

* Christian Holton <cmholton@gmail.com>





