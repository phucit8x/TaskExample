package Model;

import java.lang.String; /**
 * Created by vinhphuc on 3/10/15.
 */

public class TaskModel {
    private String name = "" ;
    private boolean checked = false ;
    public TaskModel() {}
    public TaskModel( String name ) {
        this.name = name ;
    }
    public TaskModel( String name, boolean checked ) {
        this.name = name ;
        this.checked = checked ;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    public String toString() {
        return name ;
    }
    public void toggleChecked() {
        checked = !checked ;
    }
}