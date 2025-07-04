package roomescape.auth.application;

import roomescape.auth.dto.AuthenticatedMember;
import roomescape.member.model.Member;

public interface TokenService {

    String create(Member member);

    AuthenticatedMember resolveAuthenticatedMember(String token);
}
