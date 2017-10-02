package neutrino.project.clientwrapper.util.ui

import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.layout.GridPane
import java.util.*

/**
 * Use it if LoginWebView don't load cookie
 */
class LoginCredentialView {

	fun showAndWait(): Optional<Pair<String, String>> {
		val dialog = Dialog<Pair<String, String>>()
		dialog.title = "Login"

		val loginButtonType = ButtonType("Login", ButtonData.OK_DONE)
		dialog.dialogPane.buttonTypes.addAll(loginButtonType, ButtonType.CANCEL)

		val grid = GridPane()
		grid.hgap = 10.0
		grid.vgap = 10.0
		grid.padding = Insets(20.0, 150.0, 10.0, 10.0)

		val username = TextField()
		username.promptText = "Username"
		val password = PasswordField()
		password.promptText = "Password"

		grid.add(Label("Username:"), 0, 0)
		grid.add(username, 1, 0)
		grid.add(Label("Password:"), 0, 1)
		grid.add(password, 1, 1)

		val loginButton = dialog.dialogPane.lookupButton(loginButtonType)
		loginButton.isDisable = true

		username.textProperty().addListener(
				{ _, _, newValue -> loginButton.isDisable = newValue.trim().isEmpty() })

		dialog.dialogPane.content = grid

		Platform.runLater({ username.requestFocus() })

		dialog.setResultConverter({ dialogButton ->
			if (dialogButton === loginButtonType) {
				return@setResultConverter Pair(username.text, password.text)
			}
			null
		})

		return dialog.showAndWait()
	}
}