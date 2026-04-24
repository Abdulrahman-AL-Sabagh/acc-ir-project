MAKEFILE_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
PROJECT_ROOT := $(MAKEFILE_DIR)

# list of source files
JAVA_SRC := ir/Parser.java \
	ir/Scanner.java \
	ir/MiniLang.java

CLASS_FILES := $(patsubst %.java,bin/%.class,$(JAVA_SRC))


all: $(CLASS_FILES)

ir/Parser.java: ir/MiniLang.ATG ir/Parser.frame ir/Scanner.frame
	java -jar $(PROJECT_ROOT)/libs/Coco.jar ir/MiniLang.ATG

bin:
	mkdir -p bin

$(CLASS_FILES): $(JAVA_SRC) bin
	javac -d bin $(JAVA_SRC)
