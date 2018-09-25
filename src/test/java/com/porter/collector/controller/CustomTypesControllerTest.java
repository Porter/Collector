package com.porter.collector.controller;

import com.google.common.collect.ImmutableList;
import com.porter.collector.db.CustomTypeDao;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.ValueTypes;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class CustomTypesControllerTest {

    @Test(expected = IllegalArgumentException.class)
    public void createUnevenArrays() {
        CustomTypeDao customTypeDao = mock(CustomTypeDao.class);
        CustomTypesController customTypesController = new CustomTypesController(customTypeDao);
        SimpleUser user = mock(SimpleUser.class);

        customTypesController.create(user, "name", ImmutableList.of(), ImmutableList.of(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createOutOfBounds() {
        CustomTypeDao customTypeDao = mock(CustomTypeDao.class);
        CustomTypesController customTypesController = new CustomTypesController(customTypeDao);
        SimpleUser user = mock(SimpleUser.class);

        customTypesController.create(user, "name", ImmutableList.of("f"), ImmutableList.of(ValueTypes.values().length));
    }

    @Test
    public void create() {
        CustomTypeDao customTypeDao = mock(CustomTypeDao.class);
        CustomTypesController customTypesController = new CustomTypesController(customTypeDao);
        SimpleUser user = mock(SimpleUser.class);

        customTypesController.create(user, "name", ImmutableList.of("f"), ImmutableList.of(2));
        verify(customTypeDao).insert(anyLong(), any(), any());
    }
}