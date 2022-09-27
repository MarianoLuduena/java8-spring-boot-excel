package ar.com.itau.seed.domain;

import ar.com.itau.seed.domain.error.EntityError;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.With;
import org.pcollections.TreePVector;

import java.util.List;

@EqualsAndHashCode(doNotUseGetters = true)
public class AccountsBatch {

    public static AccountsBatch create(final String sourceName) {
        return new AccountsBatch(null, sourceName, TreePVector.empty(), TreePVector.empty());
    }

    @With
    @Getter
    private final Long id;
    @NonNull
    @Getter
    private final String sourceName;
    @NonNull
    private TreePVector<EntityError> errors;
    @NonNull
    private TreePVector<Employee> employees;

    private AccountsBatch(
            final Long id,
            @NonNull final String sourceName,
            @NonNull final TreePVector<EntityError> errors,
            @NonNull final TreePVector<Employee> employees
    ) {
        this.id = id;
        this.sourceName = sourceName;
        this.errors = errors;
        this.employees = employees;
    }

    public void addError(final EntityError error) {
        errors = errors.plus(error);
    }

    public void addEmployee(final Employee employee) {
        employees = employees.plus(employee);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<EntityError> getErrors() {
        return errors;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public int errorCount() {
        return errors.size();
    }

    public int employeeCount() {
        return employees.size();
    }

    @Override
    public String toString() {
        return "AccountsBatch{" +
                "id=" + id +
                ", sourceName='" + sourceName + '\'' +
                '}';
    }

}
