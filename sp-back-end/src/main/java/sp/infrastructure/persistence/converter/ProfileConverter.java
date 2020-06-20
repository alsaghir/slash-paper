package sp.infrastructure.persistence.converter;

import sp.domain.model.profile.Profile;
import sp.infrastructure.persistence.entity.ProfileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProfileConverter {

  private final PrivilegeConverter privilegeConverter;

  @Autowired
  public ProfileConverter(PrivilegeConverter privilegeConverter) {
    this.privilegeConverter = privilegeConverter;
  }

  public Set<ProfileEntity> toPersistenceEntitySet(Set<Profile> profiles) {
    return profiles.parallelStream().map(this::toPersistenceEntity).collect(Collectors.toSet());
  }

  public ProfileEntity toPersistenceEntity(Profile profile) {
    return new ProfileEntity()
        .setId(profile.id())
        .setName(profile.name())
        .setPrivilegeEntitySet(
            profile.privileges() == null
                ? null
                : this.privilegeConverter.toPrivilegeEntitySet(profile.privileges()));
  }

  public Set<Profile> toDomainSet(Set<ProfileEntity> profileEntities) {
    return profileEntities.parallelStream().map(this::toDomain).collect(Collectors.toSet());
  }

  public Profile toDomain(ProfileEntity profileEntity) {
    return new Profile(profileEntity.getId(), profileEntity.getName())
        .assignPrivileges(
            profileEntity.getPrivilegeEntitySet() == null
                ? null
                : this.privilegeConverter.toDomainSet(profileEntity.getPrivilegeEntitySet()));
  }
}
