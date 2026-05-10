package es.uji.ei1027.ovi.validator;

import es.uji.ei1027.ovi.model.Contract;
import org.springframework.lang.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

public class ContractValidator implements Validator {

    private static final String ERROR_OBLIGATORI = "obligatori";

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return Contract.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        Contract contract = (Contract) target;

        if (contract.getStartServiceDate() == null) {
            errors.rejectValue("startServiceDate", ERROR_OBLIGATORI,
                    "La data d'inici del servei és obligatòria");
        } else if (contract.getStartServiceDate().isBefore(LocalDate.now())) {
            errors.rejectValue("startServiceDate", "passada",
                    "La data d'inici no pot ser anterior a avui");
        }

        if (contract.getEndServiceDate() == null) {
            errors.rejectValue("endServiceDate", ERROR_OBLIGATORI,
                    "La data de fi del servei és obligatòria");
        } else if (contract.getStartServiceDate() != null
                && contract.getEndServiceDate().isBefore(contract.getStartServiceDate())) {
            errors.rejectValue("endServiceDate", "ordre",
                    "La data de fi ha de ser posterior o igual a la data d'inici");
        }

        if (contract.getDocumentURL() == null || contract.getDocumentURL().trim().isEmpty()) {
            errors.rejectValue("documentURL", ERROR_OBLIGATORI,
                    "L'URL del document del contracte és obligatòria");
        } else if (contract.getDocumentURL().length() > 255) {
            errors.rejectValue("documentURL", "longitud",
                    "L'URL no pot superar els 255 caràcters");
        }
    }
}
