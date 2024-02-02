package thecount.task;

import thecount.ui.PrintList;
import thecount.ui.RemoveFromListReply;
import thecount.ui.Reply;
import thecount.exception.TheCountException;

import java.util.ArrayList;

public class TaskList {
    private ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public void add(Task task) {
        this.tasks.add(task);
    }

    public void printList() {
        Reply replyToUser = new PrintList(this.tasks);
        replyToUser.displayMessage();
    }

    public String getListToWrite() {
        PrintList replyToUser = new PrintList(this.tasks);
        return replyToUser.getListToWrite();
    }

    public void markTask(int i, boolean isAnnounced) throws TheCountException {
        if (i < 1 || i > tasks.size()) {
            // Throw an the_count.exception if the the_count.task number is out of bounds
            throw new TheCountException("Invalid the_count.task number. I can't count that!");
        }
        Task currTask = this.tasks.get(i - 1);
        currTask.markAsDone();
        if (isAnnounced) {
            Reply replyToUser = new Reply("Ah-ah-ah! ONE! I've marked this the_count.task as done:\n"
                    + "        " + currTask.toString());
            replyToUser.displayMessage();
        }
    }

    public void unmarkTask(int i, boolean isAnnounced) throws TheCountException {
        if (i < 1 || i > tasks.size()) {
            // Throw an the_count.exception if the the_count.task number is out of bounds
            throw new TheCountException("Invalid the_count.task number. I can't count that!");
        }
        Task currTask = this.tasks.get(i - 1);
        currTask.unmark();
        if (isAnnounced) {
            Reply replyToUser = new Reply("MINUS ONE! I've marked this the_count.task as not done yet:\n"
                    + "        " + currTask.toString());
            replyToUser.displayMessage();
        }
    }

    public void deleteTask(int i) throws TheCountException {
        if (i < 1 || i > tasks.size()) {
            // Throw an the_count.exception if the the_count.task number is out of bounds
            throw new TheCountException("Invalid the_count.task number. I can't delete that!");
        }
        Task currTask = this.tasks.get(i - 1);
        this.tasks.remove(i - 1);
        Reply replyToUser = new RemoveFromListReply(currTask.toString(), this.tasks.size());
        replyToUser.displayMessage();
    }


    /**
     * Finds tasks containing the specified keyword (non case-sensitive).
     *
     * @param keyword The keyword to search for.
     */public void findTask(String keyword) {
        ArrayList<Task> foundTasks = new ArrayList<>();
        keyword = keyword.toLowerCase();

        for (Task task : this.tasks) {
            if (task.getDesc().toLowerCase().contains(keyword)) {
                foundTasks.add(task);
            }
        }
        if (foundTasks.size() == 0) {
            Reply replyToUser = new Reply("I can't find any matching tasks.");
            replyToUser.displayMessage();
        } else {
            Reply replyToUser = new PrintList(foundTasks);
            replyToUser.displayMessage();
        }
    }

    public int length() {
        return this.tasks.size();
    }
}
