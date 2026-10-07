# ApexStore - E-Commerce Distribuido con Java + ICE

**Requisitos previos:**
- Java 17
- PostgreSQL 12 - Local
- Gradle 8.6

### 1. Preparar PostgreSQL
```bash
psql -U postgres -c "CREATE DATABASE apexstore_db;"
psql -U postgres -c "CREATE USER apexstore_user WITH PASSWORD 'apexstore_pass123';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE apexstore_db TO apexstore_user;"
```

### 2. Compilar
```bash
cd apexstore
bash gradlew clean build
```

### 3. Ejecutar (en 4 terminales diferentes)

**Primer terminal: Base de Datos**
```bash
cd apexstore && bash gradlew -p baseDatos run
```

**Segunda terminal: Pasarelas**
```bash
cd apexstore && bash gradlew -p pasarelas run
```

**Tercera terminal: E-Commerce**
```bash
cd apexstore && bash gradlew -p ecommerce run
```

**Cuarta terminal: Clientes**
```bash
cd apexstore && bash gradlew -p clientes run
```

**Estructura del Proyecto**

apexstore/
├── common/                 
├── clientes/             
├── ecommerce/             
├── pasarelas/            
└── baseDatos/            

