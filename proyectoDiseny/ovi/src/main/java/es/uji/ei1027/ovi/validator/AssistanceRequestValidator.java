package es.uji.ei1027.ovi.validator;

import es.uji.ei1027.ovi.model.AssistanceRequest;
import org.springframework.lang.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class AssistanceRequestValidator implements Validator {

    private static final String ERROR_OBLIGATORI = "obligatori";
    private static final String ERROR_LONGITUD = "longitud";

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return AssistanceRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        AssistanceRequest request = (AssistanceRequest) target;

        if (request.getServiceLocation() == null || request.getServiceLocation().trim().isEmpty()) {
            errors.rejectValue("serviceLocation", ERROR_OBLIGATORI,
                    "La localització del servei és obligatòria");
        } else if (request.getServiceLocation().length() > 200) {
            errors.rejectValue("serviceLocation", ERROR_LONGITUD,
                    "La localització no pot superar els 200 caràcters");
        }

        if (request.getRequiredAssistance() == null || request.getRequiredAssistance().trim().isEmpty()) {
            errors.rejectValue("requiredAssistance", ERROR_OBLIGATORI,
                    "La descripció de l'assistència requerida és obligatòria");
        } else if (request.getRequiredAssistance().length() > 2000) {
            errors.rejectValue("requiredAssistance", ERROR_LONGITUD,
                    "La descripció no pot superar els 2000 caràcters");
        }
    }
}
