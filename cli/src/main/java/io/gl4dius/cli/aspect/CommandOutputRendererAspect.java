package io.gl4dius.cli.aspect;

import io.gl4dius.cli.render.CommandOutputRenderer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CommandOutputRendererAspect {

    private final CommandOutputRenderer commandOutputRenderer;

    @Around("@annotation(org.springframework.shell.core.command.annotation.Command)")
    public Object render(@NonNull ProceedingJoinPoint joinPoint) throws Throwable {
        Object commandResult = joinPoint.proceed();

        var output = switch (commandResult) {
            case null -> "";
            case String stringOutput -> stringOutput;
            case Integer integerOutput -> String.valueOf(integerOutput);
            default -> this.commandOutputRenderer.render(commandResult);
        };

        return new AttributedString(
                output,
                AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)
        ).toAnsi();
    }
}
