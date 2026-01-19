# SpellingA+: Project Specifications

Problem:\
\
Developers living in a command-line environment who write programs and applications need to create and maintain documentation. These documentation files contain many paragraphs of information, where spelling mistakes can appear. In the command line environment, developers are limited to the *aspell* and *hunspell* spell checkers. While they offer a full feature set, they are limited to manual correction for every misspelled word, regardless of the number of matches.\
\
Solution:\
\
Our project hopes to remedy this deficiency in the command line environment by creating a simple, user-controlled-varying-level-automous English language spell checker for comments in Java source files. Our spell checker can check the spelling of comment contents and compare words that do not appear in our dictionary. Variables in the source code file will be added to a session dictionary exclusion list, in addition to Java functions so these are not flagged as spelling errors. \
\
There is a fully automatic mode and a semi-automatic mode. In the fully automatic mode, no corrections are made manually, and the best choice will be decided using the number of different letters, the number of uses, and the keyboard positions of the letters. In the semi-automatic mode, there will be accuracy settings that the user can choose, and manual correction will happen if the number of different letters is beyond the user’s preset. \
\
Our spell checker also has a user-defined dictionary that the user can add custom words to. The spell checker creates a backup of the original file by default, but can also be configured to run in a direct overwrite mode without a backup.
