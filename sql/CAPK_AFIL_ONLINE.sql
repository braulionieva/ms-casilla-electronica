CREATE OR REPLACE PACKAGE SISCAS.CAPK_AFIL_ONLINE AS
 -- --------------------------------------------------------------------------------------------
  -- Sistema              : Sistema SISCAS Electrónica web - ONLINE
  -- Descripcion          : Package table CAPK_AFIL_ONLINE
  -- Fecha de Creacion    : 20/04/2024
  -- Autor                : Manuel
  -- Observacion          :
  -- Version              : 2.0
  -- Fixes   :a

PROCEDURE  SP_GENERAR_CODIGO(
                        IN_OPCION IN VARCHAR2,
                        IN_V_CORREO IN VARCHAR2,
                        PI_V_CELULAR IN VARCHAR2,
                        PI_V_NUMERO_DOC IN VARCHAR2,
                        IN_CODIGO_GEN IN VARCHAR2,
                        OUT_CODIGO_SMS OUT VARCHAR2,
                        PO_NOMBRE_COMPLETO OUT VARCHAR2,
                        OUT_CODIGO OUT VARCHAR2,
                        OUT_MSG OUT VARCHAR2);



END CAPK_AFIL_ONLINE;

CREATE OR REPLACE PACKAGE BODY SISCAS.CAPK_AFIL_ONLINE AS

EXIST_COD_VIG EXCEPTION;  -- Existe código vigente, el código que se envió por sms
CAD_INV_COD EXCEPTION; -- Código incorrecto, o ya caducó

PROCEDURE SP_GENERAR_CODIGO(
                        IN_OPCION IN VARCHAR2,
                        IN_V_CORREO IN VARCHAR2,
                        PI_V_CELULAR IN VARCHAR2,
                        PI_V_NUMERO_DOC IN VARCHAR2,
                        IN_CODIGO_GEN IN VARCHAR2,
                        OUT_CODIGO_SMS OUT VARCHAR2,
                        PO_NOMBRE_COMPLETO OUT VARCHAR2,
                        OUT_CODIGO OUT VARCHAR2,
                        OUT_MSG OUT VARCHAR2) AS

    ERR_NUM NUMBER;
    ERR_MSG VARCHAR2(255);
    VALIDADOR_COUNT NUMBER;
    CANT_MINUT_REENVIO NUMBER := 5;  -- Cantidad de minutos que debe esperar el usuario para hacer el reenvío de código
    CANT_MINUT_CADUCA NUMBER := 5;  -- Cantidad de minutos que debe esperar el usuario para hacer el reenvío de código
    STATUS_CONSUMO_SV  SISCAS.SITT_CODIGO.FL_C_INDICADOR%TYPE:= 'C';
    STATUS_CONSUMO_V  SISCAS.SITT_CODIGO.FL_C_INDICADOR%TYPE:= 'V';
   V_N_COUNT NUMBER;
    BEGIN

	  SELECT COUNT(*) INTO V_N_COUNT FROM SISCAS.SITV_CASILLA sc WHERE SC.NU_V_TELEFONO = PI_V_CELULAR;
	 DBMS_OUTPUT.PUT_LINE('Celular : ' || IN_OPCION);
	 IF V_N_COUNT > 0 THEN
		  OUT_CODIGO := '1';
          OUT_MSG := 'El número de celular ''' || PI_V_CELULAR || ''' ya se encuentra registrado.';
          RETURN;
	    END IF;


	 V_N_COUNT := 0;


	SELECT COUNT(*) INTO V_N_COUNT FROM SISCAS.SITV_CASILLA sc WHERE SC.DE_V_CORREO =  IN_V_CORREO;
	 IF V_N_COUNT > 0 THEN
		  OUT_CODIGO := '1';
          OUT_MSG := 'El correo ''' || IN_V_CORREO || ''' ya se encuentra registrado.';
          RETURN;
	    END IF;




/***********************************************************************************************
  NOMBRE:      SP_GENERAR_CODIGO_SMS
  PROPOSITO:   Procedimiento para generar el código de envío SMS al celular, utilizando un MS de envio de mensaje de texto

  PARAMETROS:
                NU_V_CELULAR   => Número de celular del usuario que solicita el envío del SMS
                IP_V_ADDRESS   => Dirección IP del usuario cliente
                OUT_CODIGO_SMS => Código generado
                OUT_CODIGO     => Parámetro de salida para el código de respuesta
                OUT_MSG        => Parámetro de salida para el mensaje de respuesta


  Ver          Fecha         Autor                          Descripcion
  ---------  -------------  --------------------  ----------------------------------------
  1.0        ----------     Frank Eder.              Creación del Procedimiento
************************************************************************************************/


    -- PARA LA OPCION _GENERAR_CODIGO
    -- Paso 1. Verficar que el nro de celular no tenga ningun código generado en el rango de CANT_MINUT, después de la fecha/hora actual
    -- Paso 2. Realizamos la inserción en la tabla


    -- PARA LA OPCION _VERIFICAR_CODIGO
    -- Paso 1. Verificar que el nro de celular, codigo
    -- Paso 2. Al verificar el status_cons cambia a 'V' ¿ Verificado?

        IF IN_OPCION='_GENERAR_CODIGO' THEN
            -- PASO 1.
            SELECT COUNT(1) INTO VALIDADOR_COUNT
            FROM SISCAS.SITT_CODIGO
            WHERE NU_V_CELULAR=IN_V_CORREO
            AND FE_D_CREACION >= (CURRENT_TIMESTAMP - NUMTODSINTERVAL(CANT_MINUT_REENVIO,'MINUTE'));
			DBMS_OUTPUT.PUT_LINE('correo : ' || IN_V_CORREO);
            IF VALIDADOR_COUNT>0 THEN
                RAISE EXIST_COD_VIG;
            END IF;

            OUT_CODIGO_SMS := FLOOR(DBMS_RANDOM.VALUE(100000, 999999));

            -- PASO 2.
            INSERT INTO SISCAS.SITT_CODIGO (
                NU_V_CELULAR,
                CO_V_AUTOGENERADO,
                IP_V_ADDRESS,
                FL_C_INDICADOR,
                FE_D_CREACION,
                FE_D_MODIFICACION
            ) VALUES (
                IN_V_CORREO,
                OUT_CODIGO_SMS,
                '::',
                STATUS_CONSUMO_SV,
                SYSTIMESTAMP,
                SYSTIMESTAMP
            );

           SELECT CPN.NO_V_CIUDADANO || ' ' || CPN.AP_V_PATERNO  || ' ' || CPN.AP_V_MATERNO INTO PO_NOMBRE_COMPLETO
    			FROM SISCFEBF.CFTV_PERSONA cp INNER JOIN SISCFEBF.CFTV_PERSONA_NATURAL cpn
    				ON  CPN.ID_V_PERSONA  = CP.ID_V_PERSONA WHERE CP.NU_V_DOCUMENTO = PI_V_NUMERO_DOC;


            DBMS_OUTPUT.PUT_LINE('Generated SMS Code: ' || OUT_CODIGO_SMS);

            IF SQL%FOUND THEN
                OUT_CODIGO:='0';
                OUT_MSG:='Código generado correctamente, el código caducará al cabo de '||CANT_MINUT_CADUCA || ' minutos.';
            END IF;

        ELSIF IN_OPCION='_VERIFICAR_CODIGO' THEN
            -- Paso 1.
            SELECT COUNT(1) INTO VALIDADOR_COUNT
            FROM SISCAS.SITT_CODIGO
            WHERE NU_V_CELULAR=IN_V_CORREO
            AND FE_D_CREACION >= (CURRENT_TIMESTAMP - NUMTODSINTERVAL(CANT_MINUT_CADUCA,'MINUTE'))
            AND CO_V_AUTOGENERADO=IN_CODIGO_GEN
            AND FL_C_INDICADOR=STATUS_CONSUMO_SV;

            IF VALIDADOR_COUNT=0 THEN
                RAISE CAD_INV_COD;
            END IF;

            -- Paso 2.
            UPDATE SISCAS.SITT_CODIGO SET FL_C_INDICADOR=STATUS_CONSUMO_V
            WHERE NU_V_CELULAR=IN_V_CORREO
            AND FE_D_CREACION >= (CURRENT_TIMESTAMP - NUMTODSINTERVAL(CANT_MINUT_CADUCA,'MINUTE'))
            AND CO_V_AUTOGENERADO=IN_CODIGO_GEN
            AND FL_C_INDICADOR=STATUS_CONSUMO_SV;

            IF SQL%FOUND THEN
                OUT_CODIGO:='0';
                OUT_MSG:='Código verificado correctamente.';
            END IF;

        END IF;

    EXCEPTION
        WHEN EXIST_COD_VIG THEN
            ERR_NUM := 2;
            ERR_MSG := 'Debe esperar '|| CANT_MINUT_REENVIO ||' minutos para volver a enviar el código de verificación.';
            OUT_CODIGO := '1';
            OUT_MSG := ERR_MSG;
        WHEN CAD_INV_COD THEN
            ERR_NUM := 3;
            ERR_MSG := 'El código ingresado es incorrecto o ya caducó. Por favor, intente nuevamente.';
            OUT_CODIGO := '1';
            OUT_MSG := ERR_MSG;
        WHEN OTHERS THEN
            ERR_NUM := SQLCODE;
            ERR_MSG := SQLERRM;
            OUT_CODIGO := '-1';
            OUT_MSG := 'ERROR: '||TO_CHAR(ERR_NUM)||' - '||ERR_MSG || ' - ' || PI_V_NUMERO_DOC ;

END SP_GENERAR_CODIGO;

END CAPK_AFIL_ONLINE;