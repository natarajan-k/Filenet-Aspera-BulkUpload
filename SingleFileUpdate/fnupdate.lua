 if env_table["startstop"] == "stop" then
    lua_log("hello")
--    os.execute ('java -classpath ".:/data/Jace.jar:/data/log4j.jar:/opt/software/upload:" BulkUploading ' ..env_table["user"]..  ' > /tmp/bulk.log')
     os.execute ('java -classpath ".:/data/Jace.jar:/data/log4j.jar:/opt/software/upload:" SingleUpload ' ..env_table["user"].. ' ' ..env_table["file"]..  ' > /tmp/single.log')
-- end
return LRET_OK
