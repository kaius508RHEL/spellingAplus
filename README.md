# SpellingA+

SpellingA+ is the ultimate command-line spell checker for comments in Java source code files. It supports both normal and JUnit comments with smart detection for function and variable names. For Linux or MacOS programmers who live in the command line, SpellingA+ allows you to check comments without leaving your terminal. SpellingA+ is configurable with different automation levels which allows words with close matches to be corrected automatically. You can select different automation levels, with settings for full manual, full automatic or somewhere in between. All changes are recorded in a changelog file and a backup is saved by default. SpellingA+ is the perfect companion for Java programmers using vim, nano, emacs or other CLI text editors.

![image](https://github.com/kaius508RHEL/spellingAplus/blob/main/sample.png)

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
