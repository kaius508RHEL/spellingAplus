# SpellingA+

Mission Statement:\
To eliminate spelling errors in documents with accuracy and precision.\
\
Problem Statement:\
Developers living in a command-line environment who write programs and applications need to create and maintain documentation. These documentation files contain many paragraphs of information, where spelling mistakes can appear. In the command line environment, developers are limited to the *aspell* and *hunspell* spell checkers. While they offer a full feature set, they are limited to manual correction for every misspelled word, regardless of the number of matches.


**Installation instructions:**
1)  Download the `unix_release.tar.xz` archive in the folder `installers` on the repo. Direct download link: https://github.com/kaius508RHEL/spellingAplus/blob/main/installer/unix_release.tar.xz.
2)  Extract the downloaded tar archive.
3)  See the `README` file in the root of the archive. It outlines the system requirements including supported operating systems, currently MacOS and Linux with bash or zsh, and required software.
       NOTE: The `README.md` in the root of the repo is not the same as the `README` in the installation archive.
5)  Open your terminal and navigate to the root of the archive.
6)  Run `./installer.sh`. If an error appears, try run `chmod +x installer.sh` first.
7)  Follow on-screen installer instructions.
8)  SpellingA+ should now be installed and accessible from any terminal windows instance. To test it, run `spellingA+ --help` from your terminal.
       NOTE: The uninstall is located in the installation directory (your `$HOME` folder by default).
 
Enjoy! \
Team Sycamore
