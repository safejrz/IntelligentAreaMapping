package com.iam.intelligentareamapping;

/**
 * Created by jaime on 11/21/15.
 */
public class Expositor
{

    private int id;
    private String nombre;
    private String beacon;
    private String telefono;
    private String email;
    private String website;
    private String descripcion;

    public int    getId         () {return id;         }
    public String getNombre     () {return nombre;     }
    public String getBeacon     () {return beacon;     }
    public String getTelefono   () {return telefono;   }
    public String getEmail      () {return email;      }
    public String getWebsite    () {return website;    }
    public String getDescripcion() {return descripcion;}

    public void setId          (int    id         ) {this.id          = id;         }
    public void setNombre      (String nombre     ) {this.nombre      = nombre;     }
    public void setBeacon      (String beacon     ) {this.beacon      = beacon;     }
    public void setTelefono    (String telefono   ) {this.telefono    = telefono;   }
    public void setEmail       (String email      ) {this.email       = email;      }
    public void setWebsite     (String website    ) {this.website     = website;    }
    public void setDescripcion (String descripcion) {this.descripcion = descripcion;}
}
