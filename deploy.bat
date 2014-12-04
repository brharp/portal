@echo off

setlocal

goto MAIN

REM ====================================================================
REM Usage
REM ====================================================================

:USAGE
echo Usage: 
echo.
echo   %0 [/R] target archive [subpath]
echo.
echo Description:
echo.
echo   Deploys a portal archive built with ``ant dist'', optionally
echo   restarting the affected servers. The portal servers are organ-
echo   ized into logical groups: dev, qa, prod, and p1-p5. Groups
echo   p1 through p5 divide the production servers into groups that
echo   can be upgraded/restarted independantly for an incremental 
echo   upgrade.
echo.
echo   The script knows which services to run on which servers.
echo.
echo Options:
echo.
echo   /R        Restart services.
echo.
echo   target    The server(s) to update. Must be one of the logical 
echo             server groups: dev, qa, prod, p1, p2, p3, p4, or p5.
echo.
echo   archive   The portal archive file (.tar.gz) to deploy.
echo.
echo   subpath   An (optional) subpath of the archive to deploy. If
echo             omitted, the entire archive is deployed.
echo.
goto END

:MAIN

REM ====================================================================
REM Environment settings.
REM ====================================================================

set SSH=plink
set USER=as_unicon


REM ====================================================================
REM Check Environment Settings
REM ====================================================================


REM ====================================================================
REM Constants
REM ====================================================================

set ACADEMUSCTL=/portal/unicon/bin/academusctl
set UNTAR=tar --same-owner -C / -zxf - 


REM ====================================================================
REM Server Definitions
REM
REM   Servers are divided into 3 groups: dev, qa, and production. 
REM Production is in turn divided into 5 phases, so it can be upgraded 
REM incrementally. It's recommended that each production server be
REM removed from the load balancer before upgrading. The 'prod' target
REM exists to upgrade all 5 phases of production.
REM ====================================================================

set DEV_ACADEMUS=snowwhite.cs.uoguelph.ca
set DEV_APACHE=
set DEV_TOMCAT=

set QA_ACADEMUS=
set QA_APACHE=sleepy.cs.uoguelph.ca
set QA_TOMCAT=dopey.cs.uoguelph.ca

set P1_ACADEMUS=
set P1_APACHE=happy.cs.uoguelph.ca
set P1_TOMCAT=

set P2_ACADEMUS=
set P2_APACHE=doc.cs.uoguelph.ca
set P2_TOMCAT=

set P3_ACADEMUS=
set P3_APACHE=
set P3_TOMCAT=bashful.cs.uoguelph.ca

set P4_ACADEMUS=
set P4_APACHE=
set P4_TOMCAT=sneezy.cs.uoguelph.ca

set P5_ACADEMUS=
set P5_APACHE=
set P5_TOMCAT=herbie.cs.uoguelph.ca

set PROD_ACADEMUS=%P1_ACADEMUS% %P2_ACADEMUS% %P3_ACADEMUS% %P4_ACADEMUS% %P5_ACADEMUS% 
set PROD_APACHE=%P1_APACHE% %P2_APACHE% %P3_APACHE% %P4_APACHE% %P5_APACHE% 
set PROD_TOMCAT=%P1_TOMCAT% %P2_TOMCAT% %P3_TOMCAT% %P4_TOMCAT% %P5_TOMCAT% 


REM ====================================================================
REM Process command line arguments.
REM ====================================================================

:NEXT_OPT
if "%1"=="/R" goto OPT_RESTART
if "%1"=="/?" goto USAGE
goto OPT_END

:OPT_RESTART
set RESTART=YES
shift
goto NEXT_OPT

:OPT_END

set TARGET=%1
shift

set ARCHIVE=%1
shift

set SUBPATH=%1
shift


REM ====================================================================
REM Check arguments.
REM ====================================================================

if not "%TARGET%"=="" goto TARGET_SET
set ARG_CHECK=FAILED
:TARGET_SET

if not "%ARCHIVE%"=="" goto ARCHIVE_SET
set ARG_CHECK=FAILED
:ARCHIVE_SET

if not "%ARG_CHECK%"=="FAILED" goto ARG_CHECK_OK
goto USAGE
:ARG_CHECK_OK


REM ====================================================================
REM Sanity Check
REM ====================================================================

if exist "%ARCHIVE%" goto ARCHIVE_EXIST
echo Error: %ARCHIVE% does not exist!
goto END
:ARCHIVE_EXIST


REM ====================================================================
REM Calculate a Time Stamp
REM ====================================================================

set DT=%DATE% %TIME: =0%

set YEAR=%DT:~10,4%
set MONTH=%DT:~4,2%
set DAY=%DT:~7,2%
set HOUR=%DT:~15,2%
set MINUTE=%DT:~18,2%

set TSTAMP=%YEAR%%MONTH%%DAY%-%HOUR%%MINUTE%


REM ====================================================================
REM Dispatch on TARGET
REM ====================================================================

if "%TARGET%"=="dev"   goto DEV
if "%TARGET%"=="qa"    goto QA
if "%TARGET%"=="prod"  goto PROD
if "%TARGET%"=="local" goto LOCAL
if "%TARGET%"=="p1"    goto P1
if "%TARGET%"=="p2"    goto P2
if "%TARGET%"=="p3"    goto P3
if "%TARGET%"=="p4"    goto P4
if "%TARGET%"=="p5"    goto P5
echo Unknown target "%TARGET%"
goto END

:LOCAL
set UNCOMPRESSED=%ARCHIVE:.gz=%
gunzip -c %ARCHIVE% > %UNCOMPRESSED%
tar -xf %UNCOMPRESSED% -C /
del %UNCOMPRESSED%
goto END

:DEV
call :UPDATE academus %DEV_ACADEMUS%
call :UPDATE apache   %DEV_APACHE%
call :UPDATE tomcat   %DEV_TOMCAT%
goto POST_DEPLOY

:QA
call :UPDATE academus %QA_ACADEMUS%
call :UPDATE apache   %QA_APACHE%
call :UPDATE tomcat   %QA_TOMCAT%
goto POST_DEPLOY

:PROD
call :UPDATE academus %PROD_ACADEMUS%
call :UPDATE apache   %PROD_APACHE%
call :UPDATE tomcat   %PROD_TOMCAT%
goto POST_DEPLOY

:P1
call :UPDATE academus %P1_ACADEMUS%
call :UPDATE apache   %P1_APACHE%
call :UPDATE tomcat   %P1_TOMCAT%
goto POST_DEPLOY

:P2
call :UPDATE academus %P2_ACADEMUS%
call :UPDATE apache   %P2_APACHE%
call :UPDATE tomcat   %P2_TOMCAT%
goto POST_DEPLOY

:P3
call :UPDATE academus %P3_ACADEMUS%
call :UPDATE apache   %P3_APACHE%
call :UPDATE tomcat   %P3_TOMCAT%
goto POST_DEPLOY

:P4
call :UPDATE academus %P4_ACADEMUS%
call :UPDATE apache   %P4_APACHE%
call :UPDATE tomcat   %P4_TOMCAT%
goto POST_DEPLOY

:P5
call :UPDATE academus %P5_ACADEMUS%
call :UPDATE apache   %P5_APACHE%
call :UPDATE tomcat   %P5_TOMCAT%
goto POST_DEPLOY

:POST_DEPLOY
echo %DATE% %TIME%: Deployed %ARCHIVE% to %TARGET%. >> C:\portal-deploy.log
goto END


REM ====================================================================
REM Update Function
REM ====================================================================

:UPDATE

set SERVICE=%1
shift
if "%SERVICE%"=="academus" goto UPDATE_NEXT
if "%SERVICE%"=="apache" goto UPDATE_NEXT
if "%SERVICE%"=="tomcat" goto UPDATE_NEXT
echo Unknown service "%SERVICE%"
goto :EOF

:UPDATE_NEXT

set HOST=%1
shift
if "%HOST%"=="" goto :EOF

if "%RESTART%"=="YES" @echo Stopping %SERVICE% on %HOST%...
if "%RESTART%"=="YES" %SSH% %USER%@%HOST% sudo %ACADEMUSCTL% stop %SERVICE%

@echo Copying files to %HOST%...
%SSH% %USER%@%HOST% %UNTAR% %SUBPATH% < %ARCHIVE%

if "%RESTART%"=="YES" @echo Starting %SERVICE% on %HOST%...
if "%RESTART%"=="YES" %SSH% %USER%@%HOST% sudo %ACADEMUSCTL% start %SERVICE%

goto UPDATE_NEXT

:END
