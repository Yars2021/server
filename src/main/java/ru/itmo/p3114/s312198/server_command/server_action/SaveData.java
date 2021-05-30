package ru.itmo.p3114.s312198.server_command.server_action;

import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.command.CommandOutput;
import ru.itmo.p3114.s312198.command.actions.Status;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SaveData extends AbstractServerCommand {
    public SaveData() {
        super("save", "(\\w+)");
    }

    public SaveData(ArrayList<String> arguments) {
        this();
        setArguments(arguments);
    }

    @Override
    public CommandOutput execute() {
        if (arguments == null || arguments.size() == 0) {
            return new CommandOutput(Status.INCORRECT_ARGUMENTS, null);
        } else {
            ArrayList<String> output = new ArrayList<>();
            File file = new File(arguments.get(0));

            try (java.io.FileWriter fileWriter = new java.io.FileWriter(arguments.get(0))) {
                for (StudyGroup studyGroup : collectionCopy) {
                    fileWriter.write(studyGroup.toCSVLine() + '\n');
                }
                output.add("Collection has been successfully saved into \"" + arguments.get(0) + "\"");
            } catch (IOException ioe) {
                if (file.exists()) {
                    output.add("Not enough rights to access file \"" + arguments.get(0) + "\"");
                } else {
                    output.add("File \"" + arguments.get(0) + "\" does not exist");
                }
                return new CommandOutput(Status.FAILED, output);
            }
            return new CommandOutput(Status.OK, output);
        }
    }
}
