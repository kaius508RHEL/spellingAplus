printf "SpellingA+ UNinstaller version 2\n"
printf "by Project Sycamore\n\n"
printf "Uninstall location: "$HOME/spellingA+""
printf "\n\nWARNING: \nThe installation of spellingA+ at "$HOME/spellingA+",\nwill be removed including all previous files and settings.\n\n"

while true; do
read -p "Do you want to proceed with the uninstall process? (y/n) " yn
case $yn in 
	[yY] ) printf "\n"; break;;
	[nN] ) echo Aborting uninstall, no files were modified.;
		exit;;
	* ) echo Invalid response, please try again.;;
esac
done
cd $HOME
if [ -d "$HOME/spellingA+" ]; then
  rm -r "$HOME/spellingA+"
  echo "Installation at $HOME/spellingA+ removed."
else
  echo "Nothing at $HOME/spellingA+ to remove."
fi

if [ "$SHELL" = "/bin/bash" ]; then
   if [ -f "$HOME/.bashrc" ]; then
	if sed --version >/dev/null 2>&1; then
		sed -i '/# SPELLINGA+ PATH/,/# PATH ADDED END/d' ~/.bashrc
	else
		sed -i '' '/# SPELLINGA+ PATH/,/# PATH ADDED END/d' ~/.bashrc
	fi
	printf "Path entry in $HOME/.bashrc removed.\n"
	source $HOME/.bashrc
    fi
elif [ "$SHELL" = "/bin/zsh" ]; then
    if [ -f "$HOME/.zshrc" ]; then
	if sed --version >/dev/null 2>&1; then
		sed -i '/# SPELLINGA+ PATH/,/# PATH ADDED END/d' ~/.zshrc
	else
		sed -i '' '/# SPELLINGA+ PATH/,/# PATH ADDED END/d' ~/.zshrc
	fi
	printf "Path entry in $HOME/.zshrc removed.\n"
	source $HOME/.zshrc
    fi
fi

if [ ! -f "$HOME/spellingA+" ]; then
	    printf "\nFinished uninstall process successfully.\nPlease run source ~/.bashrc or source ~/.zshrc and restart your terminal shell so PATH changes can come into effect.\nTo reinstall spellingA+, run install again.\n"
fi


