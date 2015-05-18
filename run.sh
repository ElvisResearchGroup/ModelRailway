#!/bin/sh

# ==========================================
# Configuration options
# ==========================================

# Home of the JMRI installation
#JMRI_HOME=/Applications/JMRI
JMRI_HOME=$HOME/pkg/JMRI

# Directory in which application class files located
BINDIR=bin

#PORT=/dev/cu.usbmodemfa131
PORT=/dev/ttyACM0

# ==========================================
# Determine OS
# ==========================================

if [ -z "$OS" ] ; then
    # start finding the architecture specific library directories
    OS=`uname -s`

    # normalize to match our standard names
    if [ "$OS" = "Linux" ] ; then
      OS="linux"
    fi

    if [ "$OS" = "Darwin" ] ; then
      OS="macosx"
    fi
fi

# ==========================================
# Build CLASSPATH
# ==========================================

CLASSPATH=$BINDIR/:$JMRI_HOME/jmri.jar

for lib in $(ls $JMRI_HOME/lib)
do
    CLASSPATH="$CLASSPATH:$JMRI_HOME/lib/$lib"
done

# ==========================================
# Build Options
# ==========================================

LIBDIR=$JMRI_HOME/lib

SYSLIBPATH=

if [ -d "${LIBDIR}/$OS" ] ; then
    SYSLIBPATH="${LIBDIR}/$OS/x86_64/"
fi

echo $SYSLIBPATH
OPTIONS="${OPTIONS} -Djava.security.policy=${LIBDIR}/security.policy"
OPTIONS="${OPTIONS} -Djava.library.path=.:$SYSLIBPATH:${LIBDIR}"

# ==========================================
# Run Application
# ==========================================

java $OPTIONS -cp $CLASSPATH modelrailway.Main $PORT
