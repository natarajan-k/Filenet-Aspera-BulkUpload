# Perpost
if [ $TYPE == Session ]; then
  if [ $STARTSTOP == Stop ]; then
    java -classpath ".:/data/Jace.jar:/data/log4j.jar:/opt/software/upload:" BulkUploading  $USER  > /tmp/p.log
  fi
fi
