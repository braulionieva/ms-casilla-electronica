package pe.gob.mpfn.casilla.notifications.repository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import pe.gob.mpfn.casilla.notifications.exception.HttpStatusResponseException;
import pe.gob.mpfn.casilla.notifications.exception.InternalServerErrorException;
import pe.gob.mpfn.casilla.notifications.model.dto.SecurityResponseDto;
import pe.gob.mpfn.casilla.notifications.model.dto.VerificationCode2FARequest;
import pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle;

import java.math.BigDecimal;
import java.util.Map;

import static java.sql.Types.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.SP_ERROR_STATUS;
import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.SP_INTERNAL_ERROR;
import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.*;

@RequiredArgsConstructor
@SuppressWarnings("unchecked")
@Repository
public class SecurityRepositoryImpl implements SecurityRepository {

    private static final Logger log = LoggerFactory.getLogger(SecurityRepositoryImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @Override
    public SecurityResponseDto generateVerificationCode2FA(VerificationCode2FARequest datos) {

        var casillaId = datos.getIdCasilla();

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withoutProcedureColumnMetaDataAccess()
                .withSchemaName(ObjetosOracle.SISCAS)
                .withCatalogName(ObjetosOracle.CAPK_SEGURIDAD)
                .withProcedureName(ObjetosOracle.CASP_GENERAR_CODIGO_VERIFICACION)
                .declareParameters(
                    new SqlParameter(PI_V_ID_CASILLA, VARCHAR),
                    new SqlParameter(PI_N_ID_TIPO_VERIFICACION, VARCHAR),
                    new SqlParameter(PI_V_IP_USUARIO, VARCHAR),
                    new SqlParameter(PI_V_DE_DISPOSITIVO, VARCHAR),
                    new SqlParameter(PI_V_CO_US_CREACION, VARCHAR),
                    new SqlOutParameter(PO_V_CO_VERIFICACION, VARCHAR),
                    new SqlOutParameter(PO_V_NU_EXPIRACION, VARCHAR),
                    new SqlOutParameter(PO_V_ERR_COD, VARCHAR),
                    new SqlOutParameter(PO_V_ERR_MSG, VARCHAR)
                );

        SqlParameterSource objParametrosIN = new MapSqlParameterSource()
                .addValue(PI_V_ID_CASILLA, datos.getIdCasilla())
                .addValue(PI_N_ID_TIPO_VERIFICACION, datos.getIdTipoVerificacion())
                .addValue(PI_V_IP_USUARIO, datos.getIpUsuario())
                .addValue(PI_V_DE_DISPOSITIVO, datos.getDispositivo())
                .addValue(PI_V_CO_US_CREACION, datos.getUsuario());

        Map<String, Object> res = jdbcCall.execute(objParametrosIN);

        if (SP_INTERNAL_ERROR.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.error("generateVerificationCode2FA casillaID {} - {}", casillaId, res.get(PO_V_ERR_MSG));
            throw new InternalServerErrorException();
        }
        if (SP_ERROR_STATUS.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.info("generateVerificationCode2FA casillaID {} - {}", casillaId, res.get(PO_V_ERR_MSG));
            throw new HttpStatusResponseException(BAD_REQUEST, res.get(PO_V_ERR_MSG).toString());
        }

        SecurityResponseDto respuesta = new SecurityResponseDto();

        respuesta.setCode((String) res.get(PO_V_ERR_COD));
        respuesta.setMessage((String) res.get(PO_V_ERR_MSG));
        respuesta.setVerificationCode((String) res.get(PO_V_CO_VERIFICACION));
        respuesta.setExpiration((String) res.get(PO_V_NU_EXPIRACION));

        return respuesta;
    }

    @Override
    public SecurityResponseDto validateVerificationCode2FA(VerificationCode2FARequest datos) {

        var casillaId = datos.getIdCasilla();

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withoutProcedureColumnMetaDataAccess()
            .withSchemaName(ObjetosOracle.SISCAS)
            .withCatalogName(ObjetosOracle.CAPK_SEGURIDAD)
            .withProcedureName(ObjetosOracle.CASP_VALIDAR_CODIGO_VERIFICACION)
            .declareParameters(
                new SqlParameter(PI_V_CO_V_VERIFICACION, VARCHAR),
                new SqlParameter(PI_V_ID_CASILLA, VARCHAR),
                new SqlParameter(PI_N_ID_TIPO_VERIFICACION, VARCHAR),
                new SqlOutParameter(PO_B_RESULTADO, NUMERIC),
                new SqlOutParameter(PO_V_ERR_COD, VARCHAR),
                new SqlOutParameter(PO_V_ERR_MSG, VARCHAR)
            );

        SqlParameterSource objParametrosIN = new MapSqlParameterSource()
                .addValue(PI_V_CO_V_VERIFICACION, datos.getCode())
                .addValue(PI_V_ID_CASILLA, datos.getIdCasilla())
                .addValue(PI_N_ID_TIPO_VERIFICACION, datos.getIdTipoVerificacion());

        Map<String, Object> res = jdbcCall.execute(objParametrosIN);

        if (SP_INTERNAL_ERROR.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.error("validateVerificationCode2FA casillaID {} - {}", casillaId, res.get(PO_V_ERR_MSG));
            throw new InternalServerErrorException();
        }

        SecurityResponseDto respuesta = new SecurityResponseDto();

        respuesta.setVerificationResponse(((BigDecimal) res.get(PO_B_RESULTADO)).intValue());
        respuesta.setCode((String) res.get(PO_V_ERR_COD));
        respuesta.setMessage((String) res.get(PO_V_ERR_MSG));

        return respuesta;
    }

    @Override
    public SecurityResponseDto registerFailedAttempt(VerificationCode2FARequest datos) {

        var casillaId = datos.getIdCasilla();

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withoutProcedureColumnMetaDataAccess()
            .withSchemaName(ObjetosOracle.SISCAS)
            .withCatalogName(ObjetosOracle.CAPK_SEGURIDAD)
            .withProcedureName(ObjetosOracle.CASP_REGISTRAR_INTENTOS_FALLIDOS)
            .declareParameters(
                new SqlParameter(PI_V_CO_USUARIO, VARCHAR),
                new SqlParameter(PI_N_ID_TIPO_INTENTO_FALLIDO, VARCHAR),
                new SqlParameter(PI_V_IP_USUARIO, VARCHAR),
                new SqlParameter(PI_V_DE_DISPOSITIVO, VARCHAR),
                new SqlParameter(PI_V_CO_US_CREACION, VARCHAR),
                new SqlOutParameter(PO_V_ERR_COD, VARCHAR),
                new SqlOutParameter(PO_V_ERR_MSG, VARCHAR)
            );

        SqlParameterSource objParametrosIN = new MapSqlParameterSource()
            .addValue(PI_V_CO_USUARIO, datos.getUsuario())
            .addValue(PI_N_ID_TIPO_INTENTO_FALLIDO, datos.getIdTipoVerificacion())
            .addValue(PI_V_IP_USUARIO, datos.getIpUsuario())
            .addValue(PI_V_DE_DISPOSITIVO, datos.getDispositivo())
            .addValue(PI_V_CO_US_CREACION, datos.getUsuario());

        Map<String, Object> res = jdbcCall.execute(objParametrosIN);

        if (SP_INTERNAL_ERROR.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.error("registerFailedAttempt casillaID {} - {}", casillaId, res.get(PO_V_ERR_MSG));
            throw new InternalServerErrorException();
        }
        if (SP_ERROR_STATUS.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.info("registerFailedAttempt casillaID {} - {}", casillaId, res.get(PO_V_ERR_MSG));
            throw new HttpStatusResponseException(BAD_REQUEST, res.get(PO_V_ERR_MSG).toString());
        }

        SecurityResponseDto respuesta = new SecurityResponseDto();

        respuesta.setCode((String) res.get(PO_V_ERR_COD));
        respuesta.setMessage((String) res.get(PO_V_ERR_MSG));

        return respuesta;
    }

    @Override
    public SecurityResponseDto checkAccount(VerificationCode2FARequest datos) {

        var casillaId = datos.getIdCasilla();

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withoutProcedureColumnMetaDataAccess()
            .withSchemaName(ObjetosOracle.SISCAS)
            .withCatalogName(ObjetosOracle.CAPK_SEGURIDAD)
            .withProcedureName(ObjetosOracle.CASP_VALIDAR_CUENTA)
            .declareParameters(
                new SqlParameter(PI_V_CO_USUARIO, VARCHAR),
                new SqlParameter(PI_N_ID_TIPO_INTENTO_FALLIDO, NUMERIC),
                new SqlParameter(PI_F_VALIDAR_BLOQUEO, CHAR),
                new SqlOutParameter(PO_N_INTENTOS_RESTANTES, NUMERIC),
                new SqlOutParameter(PO_V_ERR_COD, VARCHAR),
                new SqlOutParameter(PO_V_ERR_MSG, VARCHAR)
            );

        SqlParameterSource objParametrosIN = new MapSqlParameterSource()
                .addValue(PI_V_CO_USUARIO, datos.getUsuario())
                .addValue(PI_N_ID_TIPO_INTENTO_FALLIDO, datos.getIdTipoVerificacion())
                .addValue(PI_F_VALIDAR_BLOQUEO, datos.getSoloBloqueo());

        Map<String, Object> res = jdbcCall.execute(objParametrosIN);

        if (SP_INTERNAL_ERROR.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.error("checkAccount casillaID {} - {}", casillaId, res.get(PO_V_ERR_MSG));
            throw new InternalServerErrorException();
        }

        SecurityResponseDto respuesta = new SecurityResponseDto();

        respuesta.setCode((String) res.get(PO_V_ERR_COD));
        respuesta.setMessage((String) res.get(PO_V_ERR_MSG));
        if ( res.get(PO_N_INTENTOS_RESTANTES) != null ) {
            respuesta.setRemainingAttempts(((BigDecimal) res.get(PO_N_INTENTOS_RESTANTES)).intValue());
        }

        return respuesta;
    }

    @Override
    public void restartAttempts(String usuario, String idCasilla, int idFlujoCasilla) {

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withoutProcedureColumnMetaDataAccess()
            .withSchemaName(ObjetosOracle.SISCAS)
            .withCatalogName(ObjetosOracle.CAPK_SEGURIDAD)
            .withProcedureName(ObjetosOracle.CASP_RESTABLECER_INTENTOS)
            .declareParameters(
                new SqlParameter(PI_V_CO_USUARIO, VARCHAR),
                new SqlParameter(PI_V_ID_CASILLA, VARCHAR),
                new SqlParameter(PI_N_ID_TIPO_VERIFICACION, VARCHAR),
                new SqlOutParameter(PO_V_ERR_COD, VARCHAR),
                new SqlOutParameter(PO_V_ERR_MSG, VARCHAR)
            );

        SqlParameterSource objParametrosIN = new MapSqlParameterSource()
                .addValue(PI_V_CO_USUARIO, usuario)
                .addValue(PI_V_ID_CASILLA, idCasilla)
                .addValue(PI_N_ID_TIPO_VERIFICACION, idFlujoCasilla);

        Map<String, Object> res = jdbcCall.execute(objParametrosIN);

        if (SP_INTERNAL_ERROR.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.error("restartAttempts casillaID {} - {}", idCasilla, res.get(PO_V_ERR_MSG));
            throw new InternalServerErrorException();
        }

        log.info("restartAttempts:{}", res.get(PO_V_ERR_COD));
        log.info("restartAttempts:{}", res.get(PO_V_ERR_MSG));

    }

}