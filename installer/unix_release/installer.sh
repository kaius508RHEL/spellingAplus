printf "SpellingA+ installer version 2\n"
printf "by Project Sycamore\n\n"
echo "Install location: "$HOME/spellingA+""
printf "\nWARNING: \nIf an existing installation of spellingA+ exists at "$HOME/spellingA+",\nthen all previous files and settings will be deleted.\n\n"

while true; do
read -p "Do you want to proceed with the installation? (y/n) " yn
case $yn in 
	[yY] ) printf "\n"; break;;
	[nN] ) echo Aborting installation, no files were modified.;
		exit;;
	* ) echo Invalid response, please try again.;;
esac
done

if [ -d "$HOME/spellingA+" ]; then
  rm -r "$HOME/spellingA+"
  echo "Existing installation at $HOME/spellingA+ removed."
fi

if [ "$SHELL" = "/bin/bash" ]; then
   if [ -f "$HOME/.bashrc" ]; then
	if sed --version >/dev/null 2>&1; then
		sed -i '/# SPELLINGA+ PATH/,/# PATH ADDED END/d' ~/.bashrc
	else
		sed -i '' '/# SPELLINGA+ PATH/,/# PATH ADDED END/d' ~/.bashrc
	fi
	printf "Existing path entry in $HOME/.bashrc removed.\n"
	source $HOME/.bashrc
    fi
elif [ "$SHELL" = "/bin/zsh" ]; then
    if [ -f "$HOME/.zshrc" ]; then
	if sed --version >/dev/null 2>&1; then
		sed -i '/# SPELLINGA+ PATH/,/# PATH ADDED END/d' ~/.zshrc
	else
		sed -i '' '/# SPELLINGA+ PATH/,/# PATH ADDED END/d' ~/.zshrc
	fi
	printf "Existing path entry in $HOME/.zshrc removed.\n"
	source $HOME/.zshrc
    fi
fi
	 
mkdir "$HOME/spellingA+"
tar -xf data.tar.xz -C $HOME/spellingA+
cd $HOME/spellingA+
chmod +x $HOME/spellingA+/spellingA+
echo "New installation at $HOME/spellingA+."

javac -d src $(find src -name "*.java")

if [ "$SHELL" = "/bin/bash" ]; then
	if [ ! -f "$HOME/.bashrc" ]; then
		touch $HOME/.bashrc
		printf "$HOME/.bashrc created.\n"
	fi
	printf "Added program path to $HOME/.bashrc\n"
	echo '# SPELLINGA+ PATH' >> ~/.bashrc
	echo 'export PATH=$HOME/spellingA+:$PATH' >> $HOME/.bashrc
	echo '# PATH ADDED END' >> ~/.bashrc
	source $HOME/.bashrc
elif [ "$SHELL" = "/bin/zsh" ]; then
       if [ ! -f "$HOME/.zshrc" ]; then
		touch $HOME/.zshrc
		printf "$HOME/.zshrc created.\n"
	fi
	printf "Added program path to $HOME/.zshrc\n"
	echo '# SPELLINGA+ PATH' >> ~/.zshrc
	echo 'export PATH=$HOME/spellingA+:$PATH' >> $HOME/.zshrc
	echo '# PATH ADDED END' >> ~/.zshrc
	source $HOME/.zshrc
fi

cd $HOME

printf "\nFinished installation successfully! \nThank you for choosing spellingA+\nPlease run source ~/.bashrc or source ~/.zshrc and restart your terminal shell so PATH changes can come into effect.\nRead the README for more information or type spellingA+ --help for more information.\n"
