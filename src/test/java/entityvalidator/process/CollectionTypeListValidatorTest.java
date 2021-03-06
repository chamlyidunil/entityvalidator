package entityvalidator.process;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import entityvalidator.ValidationError;
import entityvalidator.entity.FieldTest;
import entityvalidator.entity.User;
import entityvalidator.type.notation.ArrayType;
import entityvalidator.type.notation.CollectionType;

public class CollectionTypeListValidatorTest {

    @Test
    public void listWithoutInternalValidation() throws Exception {
        FieldTest fieldTest = new FieldTest();
        Field field = fieldTest.getClass().getDeclaredField("usersListWithoutInternalValidation");

        CollectionType collectionType = Mockito.mock(CollectionType.class);
        Mockito.when(collectionType.maxLength()).thenReturn(1);
        Mockito.when(collectionType.minLength()).thenReturn(0);

        //Empty User List
        CollectionTypeValidator collectionTypeValidator = new CollectionTypeValidator(field, fieldTest, collectionType);
        List<ValidationError> errors = new ArrayList<>();
        collectionTypeValidator.validate(errors);
        Assert.assertThat(errors, IsCollectionWithSize.hasSize(0));

        //List minimum size 1 with empty list
        Mockito.when(collectionType.minLength()).thenReturn(1);
        collectionTypeValidator = new CollectionTypeValidator(field, fieldTest, collectionType);
        collectionTypeValidator.validate(errors);
        Assert.assertThat(errors, IsCollectionWithSize.hasSize(1));

        //List minimum size 0, maximum size 1 with size list 1
        Mockito.when(collectionType.minLength()).thenReturn(0);
        errors = new ArrayList<>();
        List<User> users = new ArrayList<>();
        users.add(new User());
        fieldTest.setUsersListWithoutInternalValidation(users);
        collectionTypeValidator = new CollectionTypeValidator(field, fieldTest, collectionType);
        collectionTypeValidator.validate(errors);
        Assert.assertThat(errors, IsCollectionWithSize.hasSize(0));

        //List minimum size 0, maximum size 1 with size list 2
        users.add(new User());
        fieldTest.setUsersListWithoutInternalValidation(users);
        collectionTypeValidator = new CollectionTypeValidator(field, fieldTest, collectionType);
        collectionTypeValidator.validate(errors);
        Assert.assertThat(errors, IsCollectionWithSize.hasSize(1));

    }

    @Test
    public void listWithInternalValidation() throws Exception {

        FieldTest fieldTest = new FieldTest();
        Field field = fieldTest.getClass().getDeclaredField("usersListWithInternalValidation");


        CollectionType collectionType = Mockito.mock(CollectionType.class);
        Mockito.when(collectionType.validateInnerEntity()).thenReturn(true);
        Mockito.when(collectionType.maxLength()).thenReturn(1);
        Mockito.when(collectionType.minLength()).thenReturn(0);
        Mockito.when(collectionType.errorDecs()).thenReturn("Invalid Collection Type");

        //List minimum size 0, maximum 1 with empty list. - no errors
        List<User> users = new ArrayList<>();
        fieldTest.setUsersListWithInternalValidation(users);
        CollectionTypeValidator collectionTypeValidator = new CollectionTypeValidator(field, fieldTest, collectionType);
        List<ValidationError> errors = new ArrayList<>();
        collectionTypeValidator.validate(errors);
        Assert.assertThat(errors, IsCollectionWithSize.hasSize(0));

        //user with invalid name- name required
        //array minimum size 0 and mx size 1- max item exceed.
        User user = new User();
        users.add(user);
        User usertwo = new User();
        users.add(usertwo);
        fieldTest.setUsersListWithInternalValidation(users);
        collectionTypeValidator = new CollectionTypeValidator(field, fieldTest, collectionType);
        errors = new ArrayList<>();
        collectionTypeValidator.validate(errors);
        Assert.assertThat(errors, IsCollectionWithSize.hasSize(1));
        Assert.assertThat(errors, CoreMatchers.hasItem(Matchers.hasProperty("fieldName",
                Matchers.equalTo("usersListWithInternalValidation"))));
        Assert.assertThat(errors, CoreMatchers.hasItem(Matchers.hasProperty("errorDescription",
                Matchers.equalTo("Invalid Collection Type"))));


        Mockito.when(collectionType.minLength()).thenReturn(4);
        Mockito.when(collectionType.maxLength()).thenReturn(4);
        collectionTypeValidator = new CollectionTypeValidator(field, fieldTest, collectionType);
        errors = new ArrayList<>();
        collectionTypeValidator.validate(errors);
        Assert.assertThat(errors, IsCollectionWithSize.hasSize(1));
        Assert.assertThat(errors, CoreMatchers.hasItem(Matchers.hasProperty("fieldName",
                Matchers.equalTo("usersListWithInternalValidation"))));
        Assert.assertThat(errors, CoreMatchers.hasItem(Matchers.hasProperty("errorDescription",
                Matchers.equalTo("Invalid Collection Type"))));

        //No max / min length erros. User array size 1- but erros in User object
        Mockito.when(collectionType.minLength()).thenReturn(1);
        Mockito.when(collectionType.maxLength()).thenReturn(4);
        collectionTypeValidator = new CollectionTypeValidator(field, fieldTest, collectionType);
        errors = new ArrayList<>();
        collectionTypeValidator.validate(errors);
        Assert.assertThat(errors, IsCollectionWithSize.hasSize(4));
        Assert.assertThat(errors, CoreMatchers.hasItem(Matchers.hasProperty("fieldName",
                Matchers.equalTo("userName"))));
        Assert.assertThat(errors, CoreMatchers.hasItem(Matchers.hasProperty("errorDescription",
                Matchers.equalTo("Mandatory field"))));
        Assert.assertThat(errors, CoreMatchers.hasItem(Matchers.hasProperty("fieldName",
                Matchers.equalTo("age"))));
        Assert.assertThat(errors, CoreMatchers.hasItem(Matchers.hasProperty("errorDescription",
                Matchers.equalTo("Invalid Value:10"))));



        //No max / min length errors. User array size 2 - one user have errors
        user.setAge(10);
        user.setUserName("Chamly One");
        collectionTypeValidator = new CollectionTypeValidator(field, fieldTest, collectionType);
        errors = new ArrayList<>();
        collectionTypeValidator.validate(errors);
        Assert.assertThat(errors, IsCollectionWithSize.hasSize(2));
        Assert.assertThat(errors, CoreMatchers.hasItem(Matchers.hasProperty("fieldName",
                Matchers.equalTo("userName"))));
        Assert.assertThat(errors, CoreMatchers.hasItem(Matchers.hasProperty("errorDescription",
                Matchers.equalTo("Mandatory field"))));
        Assert.assertThat(errors, CoreMatchers.hasItem(Matchers.hasProperty("fieldName",
                Matchers.equalTo("age"))));
        Assert.assertThat(errors, CoreMatchers.hasItem(Matchers.hasProperty("errorDescription",
                Matchers.equalTo("Invalid Value:10"))));

        //No max / min length errors. User array size 2 - no user have errors
        usertwo.setAge(10);
        usertwo.setUserName("Chamly Two");
        collectionTypeValidator = new CollectionTypeValidator(field, fieldTest, collectionType);
        errors = new ArrayList<>();
        collectionTypeValidator.validate(errors);
        Assert.assertThat(errors, IsCollectionWithSize.hasSize(0));


    }

}
