package edu.jl.authenticationandauthorizationspring.mock;

import edu.jl.authenticationandauthorizationspring.model.PermissionModel;
import edu.jl.authenticationandauthorizationspring.model.UserModel;

import java.util.List;

public abstract class UserMock {
    protected UserModel validAdminUser = new UserModel(
                    "first_admin",
                    "23ldb)-3678Bvmd_@34lmvn*9344pldknbvh3&&*3499kjnD",
            List.of(
                    new PermissionModel("ROLE_ADMIN"),
                    new PermissionModel("ROLE_GUEST"))

    );
    protected UserModel adminUserWithNullUsername = new UserModel(
            null,
            "23ldb)-3678Bvmd_@34lmvn*9344pldknbvh3&&*3499kjnD",
            List.of(
                    new PermissionModel("ROLE_ADMIN"),
                    new PermissionModel("ROLE_GUEST"))

    );
    protected UserModel adminUserWithEmptyUsername = new UserModel(
            "",
            "23ldb)-3678Bvmd_@34lmvn*9344pldknbvh3&&*3499kjnD",
            List.of(
                    new PermissionModel("ROLE_ADMIN"),
                    new PermissionModel("ROLE_GUEST"))

    );
    protected UserModel adminUserWithBlankUsername = new UserModel(
            "            ",
            "23ldb)-3678Bvmd_@34lmvn*9344pldknbvh3&&*3499kjnD",
            List.of(
                    new PermissionModel("ROLE_ADMIN"),
                    new PermissionModel("ROLE_GUEST"))

    );
    protected UserModel adminUserWithShortUsername = new UserModel(
            "            ",
            "23ldb)-3678Bvmd_@34lmvn*9344pldknbvh3&&*3499kjnD",
            List.of(
                    new PermissionModel("ROLE_ADMIN"),
                    new PermissionModel("ROLE_GUEST"))

    );
    protected UserModel adminUserWithUnregisteredUsername = new UserModel(
            "second_admin",
            "23ldb)-3678Bvmd_@34lmvn*9344pldknbvh3&&*3499kjnD",
            List.of(
                    new PermissionModel("ROLE_ADMIN"),
                    new PermissionModel("ROLE_GUEST"))

    );
    protected UserModel adminUserWithWrongPassword = new UserModel(
            "first_admin",
            "abcdABCD1234^´-_",
            List.of(
                    new PermissionModel("ROLE_ADMIN"),
                    new PermissionModel("ROLE_GUEST"))

    );
    protected UserModel guestUser = new UserModel(
            "first_guest",
            "sdf<mJ739ISHCB389Ko3vLçd0)*3evh^9",
            List.of(new PermissionModel("ROLE_GUEST"))
    );

    protected UserModel validNewUser = new UserModel(
            "new_user",
            "slfsd*(*@@dfljffKK*6vCF",
            List.of(new PermissionModel("ROLE_GUEST"))
    );

    protected UserModel newUserWithNullUsername = new UserModel(
            null,
            "slfsd*(*@@dfljffKK*6vCF",
            List.of(new PermissionModel("ROLE_GUEST"))
    );

    protected UserModel newUserWithEmptyUsername = new UserModel(
            "",
            "slfsd*(*@@dfljffKK*6vCF",
            List.of(new PermissionModel("ROLE_GUEST"))
    );

    protected UserModel newUserWithBlankUsername = new UserModel(
            "     ",
            "slfsd*(*@@dfljffKK*6vCF",
            List.of(new PermissionModel("ROLE_GUEST"))
    );

    protected UserModel newUserWithShortUsername = new UserModel(
            "1234",
            "slfsd*(*@@dfljffKK*6vCF",
            List.of(new PermissionModel("ROLE_GUEST"))
    );

    protected UserModel newUserWithNumericPassword = new UserModel(
            "new_user",
            "12345678",
            List.of(new PermissionModel("ROLE_GUEST"))
    );

    protected UserModel newUserWithLowercasePassword = new UserModel(
            "new_user",
            "abcdefgh",
            List.of(new PermissionModel("ROLE_GUEST"))
    );

    protected UserModel newUserWithUppercasePassword = new UserModel(
            "new_user",
            "ABCDEFGH",
            List.of(new PermissionModel("ROLE_GUEST"))
    );

    protected UserModel newUserWithSpecialCharPassword = new UserModel(
            "new_user",
            "_-`^@#><",
            List.of(new PermissionModel("ROLE_GUEST"))
    );

    protected UserModel newUserWithNullPassword = new UserModel(
            "new_user",
            null,
            List.of(new PermissionModel("ROLE_GUEST"))
    );

    protected UserModel newUserWithEmptyPassword = new UserModel(
            "new_user",
            "",
            List.of(new PermissionModel("ROLE_GUEST"))
    );

    protected UserModel newUserWithBlankPassword = new UserModel(
            "new_user",
            "        ",
            List.of(new PermissionModel("ROLE_GUEST"))
    );

}
