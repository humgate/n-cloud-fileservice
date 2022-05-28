@REM run command for nginx docker container launching (on Windows host)
@REM uses Windows cmd syntax
@REM first -v binds host folder to container folder, so container can use static html files from it
docker run -it ^
    --rm ^
    -d ^
    -v D:\JProjects\netology\datasecurity\cloud-service\src\main\resources\frontend\dist:/usr/share/nginx/html ^
    -p 80:80 ^
    --name nginxserv ^
    nginx