# Chess Move Calculator Application

## Description
The application calculates the possible moves for both colors or, based on choice, only one color. Input is the so-called **FEN** (Forsyth-Edwards Notation) string which represents a valid chess position.

- In FEN strings, each rank should be separated by '/'.
- There must be exactly 8 ranks (since there are 8 rows on the chessboard).
- The amount of empty squares is represented by numbers 1-8.
- Pieces are represented by letters as follows:

| Piece      | White | Black |
|------------|-------|-------|
| **King**   |   K   |   k   |
| **Queen**  |   Q   |   q   |
| **Rook**   |   R   |   r   |
| **Bishop** |   B   |   b   |
| **Knight** |   N   |   n   |
| **Pawn**   |   P   |   p   |

The starting position can be expressed in **simplified FEN** format as follows:  
`rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR`

The **full FEN** format, in addition to the piece placement, also contains:
- The active color (who moves next)
- The castling rights
- The possible en-passant targets
- The halfmove clock
- The fullmove number

For example:  
`rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1`

---

## Usage

### In IDE
Put `<FEN_STRING>` as the first and only argument.  
> If neither `w` nor `b` is provided as the active color, the application calculates moves for **both** colors.

### From console with build
1. Run:
`mvn clean package` or `mvn clean package -Pfat-jar`

(the latter builds a jar with all dependencies included)

2. Then run:
`java -jar <JAR_FILE_NAME> "<FEN_STRING>"`

in the `target` folder.

### From console without build

`java -cp target\classes com.chessmove.application.ChessMoveCalculator "<FEN_STRING>"`

---

## Arguments

### `<FEN_STRING>`
The FEN notation of the chess board position and other relevant settings.  
When entering, you can simply put a `-` (hyphen) for any unknown setting.

**Parts of FEN are:**
1. Piece placement (e.g., `rnbqkbnr/pppp1ppp/8/4pP2/8/8/PPPP1PPP/R3K2R`)
2. Active player's color: `w` or `b` (white or black), or `-`
3. Castling rights: `K` and/or `Q` for white, `k` and/or `q` for black, or `-`
4. En-passant target: e.g., `e6`, or `-`
5. Halfmove clock: `0` (or any number), or `-`
6. Fullmove number: `0` (or any number), or `-`

---

## Notes
- If you do not enter any parameters other than the piece placement, or if all are marked with a hyphen (`-`), then the application uses a simplified display.
- The application **automatically declares a draw** based on:
  1. **50-move rule** (no capture has been made and no pawn has been moved in the last fifty moves).
  2. **Extended automatic draw rule** (if only two kings, or two kings and not more than one bishop or one knight on both kings' sides, remain on the board).

---

## Examples

- Full-FEN position: `rnbqkbnr/pppp1ppp/8/4pP2/8/8/PPPP1PPP/R3K2R w KQkq e6 0 3`
- Simplified FEN position by incomplete data: `rnbqkbnr/pppp1ppp/8/4pP2/8/8/PPPP1PPP/R3K2R - - - - -`
- An interesting position: `rnbqkbnr/pppp1ppp/8/4pP2/8/8/PPPP1PPP/R3K2R`







