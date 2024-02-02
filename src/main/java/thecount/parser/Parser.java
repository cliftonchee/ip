package thecount.parser;

import java.time.format.DateTimeParseException;
import java.util.Scanner;

import thecount.storage.Storage;
import thecount.task.TaskList;
import thecount.task.ToDo;
import thecount.task.Deadline;
import thecount.task.Event;
import thecount.ui.Goodbye;
import thecount.ui.Reply;
import thecount.exception.TheCountException;

/**
 * Parses user input and executes corresponding commands.
 */
public class Parser {
    private TaskList tasks;
    private Scanner scanner;
    private Storage loader;

    /**
     * Represents the types of commands.
     */
    public enum CommandType {
        BYE, LIST, MARK, UNMARK, TODO, DEADLINE, EVENT, DELETE, INVALID, FIND
    }

    /**
     * Constructs a Parser object with the task list and storage.
     *
     * @param tasks The task list to be managed.
     * @param loader The storage instance for loading and saving tasks.
     */
    public Parser(TaskList tasks, Storage loader) {
        this.tasks = tasks;
        this.scanner = new Scanner(System.in);
        this.loader = loader;
    }

    /**
     * Parses user input and executes corresponding commands.
     */
    public void parse() {
        String userInput = scanner.nextLine();

        // Checks for BYE command
        while (true) {
            switch (getCommandType(userInput)) {
                case BYE:
                    new Goodbye().displayMessage();
                    scanner.close();
                    System.exit(0);
                    break;
                case LIST:
                    tasks.printList();
                    break;
                case MARK:
                    handleMarkTask(userInput, tasks);
                    break;
                case UNMARK:
                    handleUnmarkTask(userInput, tasks);
                    break;
                case TODO:
                    handleTodoTask(userInput, tasks);
                    break;
                case DEADLINE:
                    handleDeadlineTask(userInput, tasks);
                    break;
                case EVENT:
                    handleEventTask(userInput, tasks);
                    break;
                case DELETE:
                    handleDeleteTask(userInput, tasks);
                    break;
                case FIND:
                    handleFindTask(userInput, tasks);
                    break;
                case INVALID:
                    handleInvalidCommand();
                    break;
                default:
                    break;
            }
            loader.write(tasks);
            userInput = scanner.nextLine();
        }
    }
    private static CommandType getCommandType(String userInput) {
        String commandName = userInput.split("\\s+")[0].toUpperCase();
        try {
            return CommandType.valueOf(commandName);
        } catch (IllegalArgumentException e) {
            return CommandType.INVALID;
        }
    }

    private static void handleMarkTask(String userInput, TaskList tasks) {
        try {
            int taskNumber = Integer.parseInt(userInput.split("\\s+")[1]);
            tasks.markTask(taskNumber, true);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            handleException("Please put a number. I can't count that!");
        } catch (TheCountException e) {
            handleException(e);
        }
    }

    private static void handleUnmarkTask(String userInput, TaskList tasks) {
        try {
            int taskNumber = Integer.parseInt(userInput.split("\\s+")[1]);
            tasks.unmarkTask(taskNumber, true);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            handleException("Please put a number. I can't count that!");
        } catch (TheCountException e) {
            handleException(e);
        }
    }

    private static void handleTodoTask(String userInput, TaskList tasks) {
        try {
            String info = getTaskInfo(userInput, " ");
            ToDo todo = new ToDo(info);
            tasks.add(todo);
            todo.displayMessage(tasks.length());
        } catch (TheCountException e) {
            handleException(e);
        }
    }

    private static void handleDeadlineTask(String userInput, TaskList tasks) {
        try {
            String info = getTaskInfo(userInput, "/by");
            String deadlineTime = getTaskTime(userInput, "/by", "deadline");
            Deadline deadline = new Deadline(info, deadlineTime);
            tasks.add(deadline);
            deadline.displayMessage(tasks.length());
        } catch (TheCountException e) {
            handleException(e, "Example: deadline assignment /by 2pm");
        } catch (DateTimeParseException e) {
            handleException("Please enter date in the format yyyy-MM-dd.");
        }
    }

    private static void handleEventTask(String userInput, TaskList tasks) {
        try {
            String info = getTaskInfo(userInput, "/from");
            String startTime = getStartTime(userInput);
            String endTime = getTaskTime(userInput, "/to", "end time");
            Event event = new Event(info, startTime, endTime);
            tasks.add(event);
            event.displayMessage(tasks.length());
        } catch (TheCountException e) {
            handleException(e, "Example: event meeting /from 2pm /to 4pm");
        }
    }

    private static void handleDeleteTask(String userInput, TaskList tasks) {
        try {
            int taskNumber = Integer.parseInt(userInput.split("\\s+")[1]);
            tasks.deleteTask(taskNumber);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | TheCountException e) {
            handleException(e);
        }
    }

    private static void handleInvalidCommand() {
        try {
            throw new TheCountException("WHAT?! I can't count that! Try another command!");
        } catch (TheCountException e) {
            handleException(e);
        }
    }

    private static void handleFindTask(String userInput, TaskList tasks) {
        try {
            String keyword = userInput.split("\\s+", 2)[1].trim();
            tasks.findTask(keyword);
        } catch (ArrayIndexOutOfBoundsException e) {
            handleException("Please enter a keyword to search for.");
        }
    }

    private static String getTaskInfo(String userInput, String delimiter) throws TheCountException {
        try {
            String info;
            if (delimiter.equals(" ")) {
                info = userInput.split("\\s+", 2)[1].trim();
            } else {
                info = userInput.split("\\s+", 2)[1].split(delimiter)[0].trim();
            }
            if (info.isEmpty()) {
                // Throw an the_count.exception if the_count.task information is not provided
                throw new TheCountException("Description of activity cannot be empty.");
            }
            return info;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new TheCountException("Description of activity cannot be empty.");
        }
    }

    private static String getTaskTime(String userInput, String delimiter, String timeType) throws TheCountException {
        try {
            return userInput.split(delimiter)[1].trim();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new TheCountException("Please fill in " + timeType + ".");
        }
    }

    private static String getStartTime(String userInput) throws TheCountException {
        try {
            return userInput.split("/from")[1].split("/to")[0].trim();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new TheCountException("Please fill in start time.");
        }
    }

    private static void handleException(Exception e) {
        Reply errorMsg = new Reply(e.getMessage());
        errorMsg.displayMessage();
    }

    private static void handleException(Exception e, String additionalMessage) {
        Reply errorMsg = new Reply(e.getMessage() + "\n      " + additionalMessage);
        errorMsg.displayMessage();
    }

    private static void handleException(String additionalMessage) {
        Reply errorMsg = new Reply(additionalMessage);
        errorMsg.displayMessage();
    }
}
