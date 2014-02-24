package com.citytechinc.canary.services

import com.google.common.collect.Lists
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Deactivate
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.osgi.service.log.LogEntry
import org.osgi.service.log.LogListener
import org.osgi.service.log.LogReaderService

@Component(immediate = true)
@Service
class LogExceptionListener implements LogListener {

    List<LogEntry> entries = Lists.newCopyOnWriteArrayList()

    @Reference
    LogReaderService logReaderService

    @Activate
    void activate() {

        logReaderService.addLogListener(this)
    }

    @Deactivate
    void deactivate() {

        logReaderService.removeLogListener(this)
    }

    @Override
    void logged(LogEntry logEntry) {

        if (logEntry.exception) {

            entries.add(logEntry)
        }
    }
}
