# JORTs Christmas Event 2022

[YouTube demo](https://youtu.be/5c_bdcUMGT0)

## Running
* Put the `ChristmasEvent2022.jar` file in your `C:\Users\%username%\DreamBot\Scripts` folder
* Launch Dreambot
* Open script launcher menu
* Select 'local'
* Run script with at least 10 inventory slots and no cape

## About

I programmed a bot for the OSRS Christmas event of 2022. I finished in about 3.5 hours, including breaks.

The demo in the video is the third test run. The first two failed, as new config values were introduced.

I can release the following if there is interest:

- programming video at normal speed
- the demo at normal speed
- the source code

## Programmers notes

- I logout after the first challenge, which puts me outside the portal. Reentering the portal teleports me to the gnome.
  I did this because the gnome was not visible from the first challenge, and I could not be arsed to figure out walking
  in an instance.
- I assume an empty inventory, we need the space for the gingerbread and rewards.
- I assume no cape equipped.
- questConfig = 3733; //config=3733 and varbit=14676
- amountOfCoalInBagConfig = 14689;
- myHopperId = 46446; // 46448: their hoppers
- brokenGingerId = 46452;

## Disclaimer

I see these events as programming challenges. I do not plan on abusing this script, as I mostly write bots for my own
enjoyment. I have never botted in a 'game breaking' way, like crashing iron ores in each world / crashing prices etc.
This video could also provide insight in how difficult it is to program a bot for Old School RuneScape.

## Tools used

- ffmpeg
- Intellij IDEA
- DreamBot
- Explv's map
- GIMP
- Some random image upscaler (Zyro)