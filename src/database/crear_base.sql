CREATE TABLE producto (
                          id_producto INTEGER GENERATED ALWAYS AS IDENTITY,
                          nombre VARCHAR(100) NOT NULL,
                          categoria VARCHAR(60) NOT NULL,
                          precio NUMERIC(10,2) NOT NULL,
                          existencia INTEGER NOT NULL DEFAULT 0,
                          CONSTRAINT producto_pk PRIMARY KEY (id_producto),
                          CONSTRAINT producto_precio_ck CHECK (precio >= 0),
                          CONSTRAINT producto_existencia_ck CHECK (existencia >= 0)
);