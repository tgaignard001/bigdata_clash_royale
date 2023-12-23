import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DeckTopK implements Writable, Cloneable {
    private String id;
    private double value;

    DeckTopK() {
    }




    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(id);
        out.writeDouble(value);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        id = in.readUTF();
        value = in.readDouble();
    }

    @Override
    public DeckTopK clone() {
        try {
            DeckTopK clone = (DeckTopK) super.clone();
            clone.id = this.id;
            clone.value = this.value;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return "Id : " + this.id +
                ", value: " + this.value;
    }
}
