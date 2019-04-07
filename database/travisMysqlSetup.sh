cat > ../src/main/resources/application.properties << EOL
{
    server.port: 8000
    management.server.port: 8001
    management.server.address: 127.0.0.1

    spring.jpa.hibernate.ddl-auto=none
    spring.datasource.url=jdbc:mysql://localhost:3306/youtube_project
    spring.datasource.username=root
    spring.datasource.password=

    server.servlet.context-path=/api

    spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
}
EOL
