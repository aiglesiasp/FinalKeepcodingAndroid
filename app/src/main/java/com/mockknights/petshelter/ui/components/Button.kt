package com.mockknights.petshelter.ui.components

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mockknights.petshelter.domain.ShelterType
import com.mockknights.petshelter.ui.detail.toDp
import com.mockknights.petshelter.ui.theme.*

/**
 * The button used in the welcome screen.
 * @param name The text to be displayed in the button.
 * @param modifier The modifier to be applied to the button.
 * @param colorButton The color of the button.
 * @param colorText The color of the text.
 * @param onClick The callback to be invoked when the button is clicked.
 */
@Composable
fun CreateWelcomeButton(
    name: String,
    modifier: Modifier,
    colorButton: Color,
    colorText: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .shadow(
                color = Color.Black,
                blurRadius = 4.dp,
                offsetY = 4.dp,
                offsetX = 0.dp,
                spread = 0.dp
            ),
        colors = ButtonDefaults.buttonColors(backgroundColor = colorButton)
    ) {
        Text(
            text = name,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
            color = colorText,
            style = MaterialTheme.typography.moderatMediumTitle,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 38.sp,
            modifier = Modifier
                .wrapContentHeight()
                .padding(vertical = 14.dp)
        )
    }
}

/**
 * Button row used in the register, detail and login screens.
 */
@Preview
@Composable
fun ButtonRow(onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .weight(2f)
        )
        KiwokoIconButton(
            name = "Guardar cambios",
            icon = 0,
            modifier = Modifier
                .fillMaxWidth()
                .weight(6f),
            onClick = onClick
        )
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .weight(2f)
        )
    }
}

/**
 * A button with a text and a trailing icon.
 * @param name The text to be displayed in the button.
 * @param icon The icon to be displayed in the button.
 * @param modifier The modifier to be applied to the button.
 * @param onClick The callback to be invoked when the button is clicked.
 */
@Composable
fun KiwokoIconButton(
    name: String,
    icon: Int,
    modifier: Modifier,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = RedKiwoko),
        modifier = modifier
            .shadow(
                color = Color.Black,
                blurRadius = 4.dp,
                offsetY = 4.dp,
                offsetX = 0.dp,
                spread = 0.dp
            )
            .clip(RoundedCornerShape(4))
    ) {
        Row(
            verticalAlignment = CenterVertically
        ) {
            Text(
                text = name,
                textAlign = TextAlign.Center,
                color = Color.White,
                style = MaterialTheme.typography.moderatButtonBold,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(6.6f)
            )
            if(icon != 0) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "Call", // decorative element
                    tint = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(3.3f)
                        .size(45.dp)
                )
            }
        }
    }
}

/**
 * A radio button group in a row to be displayed along with a label.
 * @param currentSelection The current selected shelter type.
 * @param onItemClick The callback to be invoked when a radio button is clicked.
 */
@Composable
fun RadioButtonsRow(
    currentSelection: ShelterType = ShelterType.PARTICULAR,
    onItemClick: (ShelterType) -> Unit = {}
) {

    val currentlySelectedShelterType = remember { mutableStateOf(currentSelection) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.toDp().dp),
    ) {
        UserDataFieldLabel(fieldLabel = "¿Qué soy?")
        RadioButtonsGroup(
            selected = currentlySelectedShelterType.value,
            onItemClick = { shelterType ->
                onItemClick(shelterType)
                currentlySelectedShelterType.value = shelterType
            }
        )
    }
}

/**
 * A radio button.
 * @param selected Whether the radio button is selected.
 * @param labelText The text to be displayed below the radio button.
 * @param modifier The modifier to be applied to the radio button.
 * @param onClick The callback to be invoked when the radio button is clicked.
 */
@Composable
fun KiwokoRadioButton(
    selected: Boolean = false,
    labelText: String = "Particular",
    modifier: Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.toDp().dp),
        modifier = modifier
            .wrapContentSize()
    ) {
        RadioButton(
            modifier = Modifier
                .size(40.toDp().dp),
            selected = selected,
            colors = RadioButtonDefaults.colors(
                selectedColor = RedKiwoko,
                unselectedColor = GrayKiwoko,
            ),
            onClick = { onClick() }
        )
        Text(
            text = labelText,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.moderatRadioButtonLabel,
        )
    }
}

/**
 * A group of radio buttons.
 * @param selected The currently selected radio button.
 * @param onItemClick The callback to be invoked when a radio button is clicked.
 */
@Preview
@Composable
fun RadioButtonsGroup(
    selected: ShelterType = ShelterType.PARTICULAR,
    onItemClick: (ShelterType) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val modifier = Modifier
            .fillMaxWidth()
            .weight(1f)

        ShelterType.values().forEach { shelterType ->
            KiwokoRadioButton(
                selected = shelterType == selected,
                labelText = shelterType.toString(),
                modifier = modifier,
                onClick = { onItemClick(shelterType) }
            ) }
    }
}

/**
 * Draws a shadow around the content.
 * @param color The color of the shadow.
 * @param borderRadius The radius of the shadow.
 * @param blurRadius The blur radius of the shadow.
 * @param offsetY The offset of the shadow in the y direction.
 * @param offsetX The offset of the shadow in the x direction.
 * @param spread The spread of the shadow.
 */
fun Modifier.shadow(
    color: Color = Color.Black,
    borderRadius: Dp = 0.dp,
    blurRadius: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    spread: Dp = 0f.dp,
    modifier: Modifier = Modifier
) = this.then(
    modifier.drawBehind {
        this.drawIntoCanvas {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            val spreadPixel = spread.toPx()
            val leftPixel = (0f - spreadPixel) + offsetX.toPx()
            val topPixel = (0f - spreadPixel) + offsetY.toPx()
            val rightPixel = (this.size.width + spreadPixel)
            val bottomPixel = (this.size.height + spreadPixel)

            if (blurRadius != 0.dp) {
                frameworkPaint.maskFilter =
                    (BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL))
            }

            frameworkPaint.color = color.toArgb()
            it.drawRoundRect(
                left = leftPixel,
                top = topPixel,
                right = rightPixel,
                bottom = bottomPixel,
                radiusX = borderRadius.toPx(),
                radiusY = borderRadius.toPx(),
                paint
            )
        }
    }
)

