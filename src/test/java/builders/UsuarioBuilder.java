package builders;

import br.ce.wcaquino.entidades.Usuario;

public class UsuarioBuilder {
    private Usuario usuario;

    private UsuarioBuilder() {
    }

    public static UsuarioBuilder umUsuario() {
        UsuarioBuilder usuarioBuilder = new UsuarioBuilder();
        usuarioBuilder.usuario = new Usuario("Rebeca");
        return usuarioBuilder;
    }

    public Usuario agora() {
        return usuario;
    }
}
