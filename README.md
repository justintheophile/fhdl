# emulation

The goal of this project is to create a general purpose library for creating emulators.
The idea is to allow for device components to be descriped in a text file with a 
hardware description language like language. Emulators should be composed of multiple 
components that should be able to communicate with each other to account for components
other than the cpu.

Components will be described by an input bus and an output bus. Components will be able to 
read instructions from the input bus and then configure the output accordingly.
