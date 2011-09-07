DEFAULT=default

FELIX_HOME=/opt/felix-framework-3.2.2/
FELIX_BIN=$(FELIX_HOME)bin/
FELIX_BUNDLE=$(FELIX_HOME)bundle/
FELIX_CACHE=$(FELIX_HOME)felix-cache/
FELIX_MAIN_CLASS=org.apache.felix.main.Main

# LOCAL_BUNDLE_LIST=drools-core-5.1.1.jar
LOCAL_BUNDLE_LIST=
BUNDLE_DIR=/tmp/plugins/

JARS_PATH=lib/
EXT_JARS_PATH=externallib/

$(DEFAULT) : runfelix 

deployandrun : deploy runfelix  

runfelix : EXT_JAR_LIST:=$(shell ls $(EXT_JARS_PATH))  
runfelix : CLASSPATH_TMP_FILE:=$(shell mktemp)  
runfelix : TMP_FILE:=$(shell mktemp)  
runfelix : 	
	for I in `ls $(FELIX_BIN)*.jar`; do echo "$$I" >> $(CLASSPATH_TMP_FILE); done
#	for I in `ls $(FELIX_BUNDLE)*.jar`; do echo "$$I" >> $(CLASSPATH_TMP_FILE); done
	echo "$(FELIX_BUNDLE)*" >> $(CLASSPATH_TMP_FILE)
	for I in `ls $(JARS_PATH)*.jar`; do echo "$$I" >> $(CLASSPATH_TMP_FILE); done
	L=`echo "$(EXT_JAR_LIST)"`; for E in $(LOCAL_BUNDLE_LIST); do L=`echo "$$L" | sed "s/\b$$E\b//g"`; done; echo $$L>$(TMP_FILE)
	for I in `cat $(TMP_FILE)`; do echo "$(EXT_JARS_PATH)$$I" >> $(CLASSPATH_TMP_FILE); done   
	cat $(CLASSPATH_TMP_FILE) | tr "\n" ":" | tr " " ":" > $(TMP_FILE) 
	sed 's/:$$//' $(TMP_FILE) > $(CLASSPATH_TMP_FILE)
	java -cp `cat $(CLASSPATH_TMP_FILE)` $(FELIX_MAIN_CLASS) $(FELIX_CACHE)
	 	
deploy : 
	rm -rf $(FELIX_CACHE)*
	cp $(BUNDLE_DIR)* $(FELIX_BUNDLE) 
	if [ -n "$(LOCAL_BUNDLE_LIST)" ]; then cd $(EXT_JARS_PATH); cp $(LOCAL_BUNDLE_LIST) $(FELIX_BUNDLE); cd - ; fi

cleanfelix :
	rm -rf $(FELIX_CACHE)*
	if [ -n "$(LOCAL_BUNDLE_LIST)" ]; then cd $(FELIX_BUNDLE); rm $(LOCAL_BUNDLE_LIST); cd -; fi
	cd $(FELIX_BUNDLE); for I in `ls $(BUNDLE_DIR)`; do rm $$I; done; cd -

clean: cleanfelix
	rm -rf $(BUNDLE_DIR)