import SwiftUI
import UIKit
import SudokuNovaSharedUI

struct ComposeSudokuView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Compose owns and updates its view hierarchy.
    }
}

struct ContentView: View {
    var body: some View {
        ComposeSudokuView()
            .ignoresSafeArea(.keyboard)
    }
}
