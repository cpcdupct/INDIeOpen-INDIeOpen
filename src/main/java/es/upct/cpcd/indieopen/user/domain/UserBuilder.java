package es.upct.cpcd.indieopen.user.domain;

import es.upct.cpcd.indieopen.utils.ObjectUtils;

/**
 * Builder class for building a User entity
 */
public class UserBuilder {

	/**
	 * Creates the UserBuilder in its first step.
	 *
	 * @return <code>UserCredentialsStep</code> instance
	 */
	public static UserCredentialsStep createBuilder() {
		return new Steps();
	}

	/**
	 * Setting user credentials setp in the UserBuilder
	 */
	public interface UserCredentialsStep {

		UserInfoStep userCredentials(String id, String completeName, String email);
	}

	/**
	 * Setting user info credentials setp in the UserBuilder
	 */
	public interface UserInfoStep {

		Build withInfo(String institution, String country);

	}

	/**
	 * Final step in the UserBuilder where all the optional setp can be reached
	 */
	public interface Build {

		/**
		 * Build the user.
		 *
		 * @return <code>User</code> instance
		 */
		UserData build();

		Build base(String base);

		UserInfoStep withUserInfo();
	}

	private static class Steps implements UserCredentialsStep, UserInfoStep, Build {

		private UserData user;

		private Steps() {
			user = new UserData();
		}

		@Override
		/**
		 * {@inheritDoc}
		 */
		public UserData build() {
			return user;
		}

		@Override
		/**
		 * {@inheritDoc}
		 */
		public UserInfoStep withUserInfo() {
			return this;
		}

		@Override
		/**
		 * {@inheritDoc}
		 */
		public Build withInfo(String institution, String country) {
			ObjectUtils.requireStringsValid(institution, country);

			user.setInstitution(institution);
			user.setCountry(country);

			return this;
		}

		@Override
		public Build base(String base) {
			ObjectUtils.requireStringValid(base);
			this.user.setBase(base);

			return this;
		}

		@Override
		public UserInfoStep userCredentials(String id, String completeName, String email) {
			ObjectUtils.requireStringsValid(id, completeName, email);

			this.user.setId(id);
			this.user.setCompleteName(completeName);
			this.user.setEmail(email);

			return this;
		}
	}
}
