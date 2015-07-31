**Scramble a picture in different ways.**

#Build
Build with Apache Maven: `mvn clean install`. Executable file are `target/image-scrambler.jar`. Copy file to your chosen destination or run it directly from this location.

#Usage
> java -jar image-scrambler.jar [options]

Executing without any options prints out the available options.

Option               | Comment
:--------------------| :-------------
-fv,--flipv          | Flipes image vertically (upside down).
-g,--gray            | Make image grey-isch (black & white).
-i,--image <arg>     | Image to scramble.
-pz,--puzzle <arg>   | Puzzles image with argument, 'columns,rows' (number off).
-v,--version         | Display version information.

For the time being, these are the restrictions:
- One, and only one, image can be processed at the time. 
- The scrambled image are saved at the current location, with the chosen options added to its name. 

#Examples
Puzzle images 'img001.jpg' with 10 columns and 25 rows. Output file './img001-puzzle.jpg'.

> java -jar image-scrambler.jar -i img001.jpg -pz 10,15

The same as above, but also make the image grey-isch, aka. black and white. Output file './img001-gray-puzzle.jpg'.

> java -jar image-scrambler.jar -i img001.jpg -pz 10,15 -g

