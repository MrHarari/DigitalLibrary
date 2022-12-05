package model;

import java.util.ArrayList;
import java.util.Objects;

public class Group {
    public final String code;
    public final String name;

    public Group(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(code, group.code) && Objects.equals(name, group.name);
    }
}
