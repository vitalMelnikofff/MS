package com.itm.space.backendresources.service;

import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest extends BaseIntegrationTest{
    private final UserService userService;

    @Autowired
    public UserServiceTest(UserService userService) {
        this.userService = userService;
    }

    @MockBean
    private Keycloak keycloak;

    @MockBean
    private List<RoleRepresentation> roleRepresentations;

    @MockBean
    private List<GroupRepresentation> groupRepresentations;


    @Test
    public void createUserTest() throws Exception {
        UserRequest userRequest = new UserRequest("test_user", "test_user@gmail.com", "test_user_password", "Luca", "Doncic");
        Response response = Response.status(Response.Status.CREATED).location(new URI("user_id")).build();
        when(keycloak.realm(anyString())).thenReturn(mock(RealmResource.class));
        when(keycloak.realm(anyString()).users()).thenReturn(mock(UsersResource.class));
        when(keycloak.realm(anyString()).users().create(any())).thenReturn(response);
        userService.createUser(userRequest);
        verify(keycloak.realm(anyString()).users(), times(1)).create(any());

    }

    @Test
    public void getUserById() {
        UserRepresentation user = new UserRepresentation();
        UUID id = UUID.randomUUID();
        user.setId(String.valueOf(id));
        user.setEmail("test_user@gmail.com");

        when(keycloak.realm(anyString())).thenReturn(mock(RealmResource.class));
        when(keycloak.realm(anyString()).users()).thenReturn(mock(UsersResource.class));
        when(keycloak.realm(anyString()).users().get(anyString())).thenReturn(mock(UserResource.class));
        when(keycloak.realm(anyString()).users().get(anyString()).toRepresentation()).thenReturn(user);
        when(keycloak.realm(anyString()).users().get(anyString()).roles()).thenReturn(mock(RoleMappingResource.class));
        when(keycloak.realm(anyString()).users().get(anyString()).roles().getAll()).thenReturn(mock(MappingsRepresentation.class));
        when(keycloak.realm(anyString()).users().get(anyString()).roles().getAll().getRealmMappings()).thenReturn(roleRepresentations);
        when(keycloak.realm(anyString()).users().get(anyString()).groups()).thenReturn(groupRepresentations);

        UserResponse response = userService.getUserById(id);
        assertEquals("test_user@gmail.com", response.getEmail());
    }

}