package pe.gob.mpfn.casilla.notifications.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import pe.gob.mpfn.casilla.notifications.exception.HttpStatusResponseException;
import pe.gob.mpfn.casilla.notifications.exception.InternalServerErrorException;
import pe.gob.mpfn.casilla.notifications.model.dto.CodeVerificationValidation;
import pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static java.sql.Types.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.SP_ERROR_STATUS;
import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.SP_INTERNAL_ERROR;
import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.PO_V_ERR_COD;
import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.PO_V_ERR_MSG;

@Repository
@Slf4j
public class SecurityRepository2 {

    private final JdbcTemplate jdbcTemplate;


    public SecurityRepository2(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<String> generarCodigoVerificacion(CodeVerificationValidation codigoVerificacionValidar) {

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withoutProcedureColumnMetaDataAccess()
                .withSchemaName(ObjetosOracle.SISCAS)
                .withCatalogName(ObjetosOracle.CAPK_SEGURIDAD)
                .withProcedureName(ObjetosOracle.CASP_GENERAR_CODIGO_VERIFICACION)
                .declareParameters(
                        new SqlParameter("PI_V_ID_CASILLA", VARCHAR),
                        new SqlParameter("PI_N_ID_TIPO_VERIFICACION", NUMERIC),
                        new SqlParameter("PI_V_IP_USUARIO", VARCHAR),
                        new SqlParameter("PI_V_DE_DISPOSITIVO", VARCHAR),
                        new SqlParameter("PI_V_CO_US_CREACION", VARCHAR),
                        new SqlOutParameter("PO_V_CO_V_VERIFICACION", VARCHAR),
                        new SqlOutParameter("PO_V_NU_EXPIRACION", VARCHAR),
                        new SqlOutParameter("PO_V_ERR_COD", VARCHAR),
                        new SqlOutParameter("PO_V_ERR_MSG", VARCHAR)
                );

        SqlParameterSource objParametrosIN = new MapSqlParameterSource()
                .addValue("PI_V_ID_CASILLA", codigoVerificacionValidar.idCasilla())
                .addValue("PI_N_ID_TIPO_VERIFICACION", codigoVerificacionValidar.idTipoVerificacion())
                .addValue("PI_V_IP_USUARIO", codigoVerificacionValidar.ipUsuario())
                .addValue("PI_V_DE_DISPOSITIVO", codigoVerificacionValidar.dispositivo())
                .addValue("PI_V_CO_US_CREACION", codigoVerificacionValidar.usuarioCreacion());

        Map<String, Object> res = jdbcCall.execute(objParametrosIN);
        log.info(res.toString());

        if (SP_INTERNAL_ERROR.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.error("%s - user: %s".formatted(res.get(PO_V_ERR_MSG).toString(), codigoVerificacionValidar.idCasilla()));
            throw new InternalServerErrorException();
        }
        if (SP_ERROR_STATUS.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.info("%s - user: %s".formatted(res.get(PO_V_ERR_MSG).toString(), codigoVerificacionValidar.idCasilla()));
            throw new HttpStatusResponseException(BAD_REQUEST, res.get(PO_V_ERR_MSG).toString());
        }


        String codigoVerificacion = (String) res.get("PO_V_CO_V_VERIFICACION");
        return Optional.ofNullable(codigoVerificacion);

    }

    public Optional<String> validarCuentaBloqueoFlujoCambioContrasena(String codigoUsuario) {

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withoutProcedureColumnMetaDataAccess()
                .withSchemaName(ObjetosOracle.SISCAS)
                .withCatalogName(ObjetosOracle.CAPK_SEGURIDAD)
                .withProcedureName(ObjetosOracle.CASP_VALIDAR_CUENTA)
                .declareParameters(
                        new SqlParameter("PI_V_CO_USUARIO", VARCHAR),
                        new SqlParameter("PI_N_ID_TIPO_INTENTO_FALLIDO", NUMERIC),
                        new SqlParameter("PI_F_VALIDAR_BLOQUEO", CHAR),
                        new SqlOutParameter("PO_N_INTENTOS_RESTANTES", VARCHAR),
                        new SqlOutParameter("PO_V_ERR_COD", VARCHAR),
                        new SqlOutParameter("PO_V_ERR_MSG", VARCHAR)
                );

        SqlParameterSource objParametrosIN = new MapSqlParameterSource()
                .addValue("PI_V_CO_USUARIO", codigoUsuario)
                .addValue("PI_N_ID_TIPO_INTENTO_FALLIDO", 1501)
                .addValue("PI_F_VALIDAR_BLOQUEO", "1");

        Map<String, Object> res = jdbcCall.execute(objParametrosIN);
        log.info(res.toString());

        if (SP_INTERNAL_ERROR.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.error("%s - user: %s".formatted(res.get(PO_V_ERR_MSG).toString(), codigoUsuario));
            throw new InternalServerErrorException();
        }
        if (SP_ERROR_STATUS.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.info("%s - user: %s".formatted(res.get(PO_V_ERR_MSG).toString(), codigoUsuario));
            throw new HttpStatusResponseException(BAD_REQUEST, res.get(PO_V_ERR_MSG).toString());
        }

        String respuesta = (String) res.get(PO_V_ERR_COD);
        return Optional.ofNullable(respuesta);

    }

    public Optional<Integer> bloqueoDeCuenta(String idCasilla) {

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withoutProcedureColumnMetaDataAccess()
                .withSchemaName(ObjetosOracle.SISCAS)
                .withCatalogName(ObjetosOracle.CAPK_SEGURIDAD)
                .withProcedureName(ObjetosOracle.CASP_BLOQUEAR_CUENTA)
                .declareParameters(
                        new SqlParameter("PI_V_ID_CASILLA", VARCHAR),
                        new SqlParameter("PI_N_ID_N_TIPO_BLOQUEO", NUMERIC),
                        new SqlOutParameter("PO_B_RESULTADO", NUMERIC),
                        new SqlOutParameter("PO_V_ERR_COD", VARCHAR),
                        new SqlOutParameter("PO_V_ERR_MSG", VARCHAR)
                );

        SqlParameterSource objParametrosIN = new MapSqlParameterSource()
                .addValue("PI_V_ID_CASILLA", idCasilla)
                .addValue("PI_N_ID_N_TIPO_BLOQUEO", 1501);

        Map<String, Object> res = jdbcCall.execute(objParametrosIN);
        log.info(res.toString());

        if (SP_INTERNAL_ERROR.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.error("%s - user: %s".formatted(res.get(PO_V_ERR_MSG).toString(), idCasilla));
            throw new InternalServerErrorException();
        }
        if (SP_ERROR_STATUS.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.info("%s - user: %s".formatted(res.get(PO_V_ERR_MSG).toString(), idCasilla));
            throw new HttpStatusResponseException(BAD_REQUEST, res.get(PO_V_ERR_MSG).toString());
        }

        BigDecimal resultado = (BigDecimal) res.get("PO_B_RESULTADO");
        Integer respuesta = resultado.intValue();
        return Optional.ofNullable(respuesta);

    }

}
