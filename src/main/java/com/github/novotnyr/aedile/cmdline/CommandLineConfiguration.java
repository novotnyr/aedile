package com.github.novotnyr.aedile.cmdline;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommandHandler;
import org.kohsuke.args4j.spi.SubCommands;

public class CommandLineConfiguration {
    @Argument(
            required = true,
            index = 0,
            usage = "Aedile command to use",
            metaVar = "Aedile command",
            handler = SubCommandHandler.class)
    @SubCommands({
        @SubCommand(name = "import", impl = FilesystemImportCommand.class),
        @SubCommand(name = "import-dc", impl = FilesystemImportDatacenterCommand.class),
        @SubCommand(name = "export", impl = FilesystemExportCommand.class),
        @SubCommand(name = "git", impl = GitImportConfiguration.class),
        @SubCommand(name = "help", impl = HelpCommandConfiguration.class),
    })
    private Object command;

    public Object getCommand() {
        return command;
    }

    public void setCommand(Object command) {
        this.command = command;
    }



}
