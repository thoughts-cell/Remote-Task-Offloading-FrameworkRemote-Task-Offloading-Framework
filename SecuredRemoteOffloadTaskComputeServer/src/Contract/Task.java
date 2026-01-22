
package Contract;

import java.io.Serializable;


public interface Task extends Serializable {
    void executeTask();
    Object getResult();
}
