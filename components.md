Description of All Components:\
SpellCheckerController
- Receives the command arguments from the user (automation level, file, etc.)
- Reads the .config file
- The part resides on both the server and the client
- Communicates with SpellCheckerModel
  - Sends the arguments to SpellCheckerModel
  - Receives the output files from SpellCheckerModel
  - Receives the list of words changed and other details from SpellCheckerModel
- Communicates with SpellCheckerView
  - Sends the list of words changed and other details to SpellCheckerView
  - Sends the output files to SpellCheckerView

SpellCheckerModel
- This is a model that corrects the file
- The model resides only on the server
- Communicates with SpellCheckerController
  - Receives the arguments from SpellCheckerController
  - Sends the output files to the SpellCheckerController
  - Sends the list of words changed and other details to SpellCheckerController

SpellCheckerView
- Displays the list of words changed and other details after running the program
- Stores the output files
- The part resides only on the client
- Communicates with SpellCheckerController
  - Receives the list of words changed and other details from SpellCheckerController
  - Receives the output files from SpellCheckerController
