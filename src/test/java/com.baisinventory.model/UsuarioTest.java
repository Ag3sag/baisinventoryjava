package com.baisinventory.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

    @Test
    public void testCrearUsuario() {
        Usuario usuario = new Usuario(1, "juan123", "1234", "admin");

        assertEquals(1, usuario.getId());
        assertEquals("juan123", usuario.getClaveAcceso());
        assertEquals("1234", usuario.getContrasena());
        assertEquals("admin", usuario.getRol());
    }
}
